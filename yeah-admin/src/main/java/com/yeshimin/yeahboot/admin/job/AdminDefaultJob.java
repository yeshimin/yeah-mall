package com.yeshimin.yeahboot.admin.job;

import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillOrderCancelMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.SeckillEventCacheVo;
import com.yeshimin.yeahboot.basic.service.storage.StorageService;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyActivityStatusEnum;
import com.yeshimin.yeahboot.data.common.properties.SeckillProperties;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.GroupBuyActivityRepo;
import com.yeshimin.yeahboot.data.repository.SysStorageRepo;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqPublisher;
import com.yeshimin.yeahboot.service.GroupBuyStockService;
import com.yeshimin.yeahboot.service.GroupBuyTradeService;
import com.yeshimin.yeahboot.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDefaultJob {

    private static final String STORAGE_CLEAN_LOCK = "yeah:job:storage:clean";
    private static final String SECKILL_QUOTA_CHECK_LOCK = "yeah:job:seckill:quota-check";
    private static final String GROUP_BUY_SETTLEMENT_LOCK = "yeah:job:group-buy:settlement";
    private static final String ORDER_REFUND_RECONCILE_LOCK = "yeah:job:order:refund-reconcile";
    private static final long JOB_LOCK_LEASE_MILLIS = 10 * 60 * 1000L;
    private static final String TRY_LOCK_LUA =
            "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2]) then return 1 else return 0 end";
    private static final String RELEASE_LOCK_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private final SysStorageRepo sysStorageRepo;
    private final GroupBuyActivityRepo groupBuyActivityRepo;

    private final StorageService storageService;
    private final CacheService cacheService;
    private final MqPublisher mqPublisher;

    private final SeckillProperties seckillProperties;
    private final GroupBuyTradeService groupBuyTradeService;
    private final GroupBuyStockService groupBuyStockService;
    private final OrderService orderService;

    /**
     * 清理过期未使用的存储记录
     * 每天1点执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoReceiveOrder() {
        executeWithJobLock(STORAGE_CLEAN_LOCK, this::cleanExpiredStorage);
    }

    /**
     * 清理过期未使用的存储记录。
     */
    private void cleanExpiredStorage() {
        log.info("清理过期未使用的存储记录-开始");

        // 本次定时任务清理总记录数
        long count = 0;

        while (true) {
            List<SysStorageEntity> list = sysStorageRepo.findListForCleanable(100);
            if (list.isEmpty()) {
                log.info("没有更多需要清理的存储记录");
                break;
            }
            log.info("本次循环可清理存储记录数：{}", list.size());
            count += list.size();

            for (SysStorageEntity entity : list) {
                log.info("清理存储记录：{}", entity);
                storageService.delete(entity.getFileKey());
            }
        }

        log.info("清理过期未使用的存储记录-结束；共清理记录数：{}", count);
    }

    /**
     * 检查秒杀抢占名额后过期未下单的；
     * 同时检查下单后过期未支付的；
     * 每1分钟执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkSeckillQuota() {
        executeWithJobLock(SECKILL_QUOTA_CHECK_LOCK, this::checkSeckillQuotaInternal);
    }

    /**
     * 检查秒杀抢占和支付超时。
     */
    private void checkSeckillQuotaInternal() {
        log.info("检查秒杀下单超时和支付超时的-开始");

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime orderTimeout = now.minusMinutes(seckillProperties.getOrderTimeoutMinutes());
        final LocalDateTime payTimeout = now.minusMinutes(seckillProperties.getPayTimeoutMinutes());

        // 查询进行中的活动集合
        Set<String> activityIds = cacheService.members(BizConsts.SECKILL_ACTIVITY_ING_KEY);
        for (String activityId : activityIds) {
            log.info("检查秒杀抢占名额后过期未下单的-活动：{}", activityId);

            // 查询活动的skuId集合
            Set<String> skuIds = cacheService.members(String.format(BizConsts.SECKILL_ACTIVITY_SKUS_KEY, activityId));
            for (String skuId : skuIds) {
                log.info("检查秒杀抢占名额后过期未下单的-sku：{}", skuId);

                // 查询sku的抢占名额用户集合
                Set<String> userIds = cacheService.members(String.format(BizConsts.SECKILL_QUOTA_KEY, skuId));
                for (String userId : userIds) {

                    // 查询事件节点缓存信息
                    String seckillEventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, userId);
                    SeckillEventCacheVo seckillEvent = cacheService.get(seckillEventKey, SeckillEventCacheVo.class);
                    if (seckillEvent == null || seckillEvent.getQuotaTime() == null) {
                        log.error("秒杀抢占名额后过期未下单的-事件节点缓存信息不存在或有误：{}", seckillEventKey);
                    } else {

                        // 当还未下单
                        if (seckillEvent.getOrderTime() == null) {
                            // 判断是否下单超时，是否超过1分钟
                            if (seckillEvent.getQuotaTime().isBefore(orderTimeout)) {
                                log.info("秒杀抢占名额后过期未下单的-用户：{}，sku：{}，下单超时", userId, skuId);
                                // 这里设置“阻止订单”的缓存key，后续操作据此key判断是否可以继续操作，包括mq订单逻辑和订单支付回调逻辑
                                cacheService.addMember(String.format(BizConsts.SECKILL_BLOCK_KEY, skuId), userId);
                                // 删除抢占名额和event信息
                                String quotaKey = String.format(BizConsts.SECKILL_QUOTA_KEY, skuId);
                                cacheService.delete(seckillEventKey, quotaKey);
                                // 释放库存
                                cacheService.increase(String.format(BizConsts.SECKILL_STOCK_KEY, skuId));
                            }
                        }
                        // 当还未支付
                        else if (seckillEvent.getPayTime() == null) {
                            // 判断是否支付超时，是否超过5分钟
                            if (seckillEvent.getOrderTime().isBefore(payTimeout)) {
                                log.info("秒杀抢占名额后过期未下单的-用户：{}，sku：{}，支付超时", userId, skuId);

                                // 这里设置“阻止订单”的缓存key，后续操作据此key判断是否可以继续操作，包括mq订单逻辑和订单支付回调逻辑
                                cacheService.addMember(String.format(BizConsts.SECKILL_BLOCK_KEY, skuId), userId);
                                // 取消订单，走mq
                                MqMessage message = new MqMessage();
                                message.setTopic(BizConsts.SECKILL_ORDER_CANCEL_TOPIC);
                                message.setPayload(SeckillOrderCancelMqPayload.of(skuId, userId).toJsonString());
                                mqPublisher.publish(message);
                            }
                        }
                    }
                }
            }

            log.info("检查秒杀下单超时和支付超时的-结束");
        }
    }

    /**
     * 补偿已结束拼团活动的结算、关单和剩余库存释放。
     */
    @Scheduled(fixedDelay = 60_000)
    public void retryGroupBuySettlement() {
        // 多实例部署时仅允许一个节点结算，避免重复关单、退款或释放库存。
        String lockToken = tryAcquireJobLock(GROUP_BUY_SETTLEMENT_LOCK);
        if (lockToken == null) {
            return;
        }
        try {
            List<GroupBuyActivityEntity> finishedActivities = groupBuyActivityRepo.lambdaQuery()
                    .eq(GroupBuyActivityEntity::getStatus, GroupBuyActivityStatusEnum.ACTIVITY_FINISHED.getValue())
                    .list();
            for (GroupBuyActivityEntity activity : finishedActivities) {
                try {
                    // 活动结束后的首次结算可能受支付网络影响失败，定时任务反复幂等补偿。
                    groupBuyTradeService.settleActivity(activity.getId());
                    groupBuyStockService.releaseUnusedStockForFinishedActivity(activity.getId());
                } catch (RuntimeException e) {
                    log.error("拼团活动结算补偿失败，activityId={}", activity.getId(), e);
                }
            }
        } finally {
            releaseJobLock(GROUP_BUY_SETTLEMENT_LOCK, lockToken);
        }
    }

    /**
     * 查询退款中的微信退款单，补偿可能丢失的退款回调。
     */
    @Scheduled(fixedDelay = 60_000)
    public void reconcileRefunds() {
        // 同一任务下统一调度普通、秒杀和拼团退款；各订单类型仍由专属服务处理补偿。
        String lockToken = tryAcquireJobLock(ORDER_REFUND_RECONCILE_LOCK);
        if (lockToken == null) {
            return;
        }
        try {
            orderService.reconcileRefundingOrders();
            groupBuyTradeService.reconcileRefundingOrders();
        } finally {
            releaseJobLock(ORDER_REFUND_RECONCILE_LOCK, lockToken);
        }
    }

    /**
     * 原子获取当前 Job 的 Redis 分布式锁。
     */
    private String tryAcquireJobLock(String lockKey) {
        String lockToken = UUID.randomUUID().toString();
        Long acquired = cacheService.executeLua(TRY_LOCK_LUA, java.util.Collections.singletonList(lockKey),
                java.util.Arrays.asList(lockToken, String.valueOf(JOB_LOCK_LEASE_MILLIS)));
        return Long.valueOf(1L).equals(acquired) ? lockToken : null;
    }

    /**
     * 在当前实例持有锁时执行任务，多实例下其他节点跳过本轮。
     */
    private void executeWithJobLock(String lockKey, Runnable job) {
        String lockToken = tryAcquireJobLock(lockKey);
        if (lockToken == null) {
            return;
        }
        try {
            job.run();
        } finally {
            releaseJobLock(lockKey, lockToken);
        }
    }

    /**
     * 仅释放当前实例持有的锁，防止锁过期并被其他实例获取后误删。
     */
    private void releaseJobLock(String lockKey, String lockToken) {
        cacheService.executeLua(RELEASE_LOCK_LUA, java.util.Collections.singletonList(lockKey),
                java.util.Collections.singletonList(lockToken));
    }

}
