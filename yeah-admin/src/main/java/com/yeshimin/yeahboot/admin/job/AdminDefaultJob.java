package com.yeshimin.yeahboot.admin.job;

import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillOrderCancelMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.SeckillEventCacheVo;
import com.yeshimin.yeahboot.admin.service.AdminPointsMallService;
import com.yeshimin.yeahboot.basic.service.storage.StorageService;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyActivityStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderTypeEnum;
import com.yeshimin.yeahboot.data.common.properties.SeckillProperties;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.GroupBuyActivityRepo;
import com.yeshimin.yeahboot.data.repository.OrderRefundRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSkuRepo;
import com.yeshimin.yeahboot.data.repository.SysStorageRepo;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqPublisher;
import com.yeshimin.yeahboot.service.GroupBuyStockService;
import com.yeshimin.yeahboot.service.GroupBuyTradeService;
import com.yeshimin.yeahboot.service.OrderService;
import com.yeshimin.yeahboot.service.PointsMallConfigService;
import com.yeshimin.yeahboot.service.RefundDispatchService;
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
    private static final String POINTS_ORDER_COMPLETE_LOCK = "yeah:job:points-order:auto-complete";
    private static final long JOB_LOCK_LEASE_MILLIS = 10 * 60 * 1000L;
    private static final String TRY_LOCK_LUA =
            "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2]) then return 1 else return 0 end";
    private static final String RELEASE_LOCK_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private final SysStorageRepo sysStorageRepo;
    private final GroupBuyActivityRepo groupBuyActivityRepo;
    private final SeckillSkuRepo seckillSkuRepo;
    private final OrderRepo orderRepo;
    private final OrderRefundRepo orderRefundRepo;

    private final StorageService storageService;
    private final CacheService cacheService;
    private final MqPublisher mqPublisher;

    private final SeckillProperties seckillProperties;
    private final GroupBuyTradeService groupBuyTradeService;
    private final GroupBuyStockService groupBuyStockService;
    private final OrderService orderService;
    private final RefundDispatchService refundDispatchService;
    private final AdminPointsMallService adminPointsMallService;
    private final PointsMallConfigService pointsMallConfigService;

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
                                String attemptId = this.resolveSeckillAttemptId(seckillEvent, userId);
                                // 只阻止本次已失效尝试，用户释放名额后可以发起新一轮抢购。
                                cacheService.addMember(String.format(BizConsts.SECKILL_BLOCK_KEY, skuId), attemptId);
                                this.releaseSeckillQuota(skuId, userId, seckillEventKey);
                            }
                        }
                        // 当还未支付
                        else if (seckillEvent.getPayTime() == null) {
                            // 判断是否支付超时，是否超过5分钟
                            if (seckillEvent.getOrderTime().isBefore(payTimeout)) {
                                log.info("秒杀抢占名额后过期未下单的-用户：{}，sku：{}，支付超时", userId, skuId);

                                String attemptId = this.resolveSeckillAttemptId(seckillEvent, userId);
                                // 阻止当前尝试的迟到消息，取消完成后新attempt不受影响。
                                cacheService.addMember(String.format(BizConsts.SECKILL_BLOCK_KEY, skuId), attemptId);
                                if (seckillEvent.getOrderId() == null) {
                                    // MQ尚未生成订单，不需要发送取消消息，直接释放本次尝试占用的名额和库存。
                                    this.releaseSeckillQuota(skuId, userId, seckillEventKey);
                                    continue;
                                }
                                // 取消订单，走mq
                                MqMessage message = new MqMessage();
                                message.setTopic(BizConsts.SECKILL_ORDER_CANCEL_TOPIC);
                                message.setPayload(SeckillOrderCancelMqPayload.of(
                                        skuId, userId, attemptId).toJsonString());
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
     * 获取抢购尝试标识，兼容升级前的缓存数据。
     */
    private String resolveSeckillAttemptId(SeckillEventCacheVo event, String userId) {
        String attemptId = event.getAttemptId();
        return attemptId == null || attemptId.trim().isEmpty() ? userId : attemptId;
    }

    /**
     * 释放一次失效抢购尝试占用的名额和Redis库存。
     */
    private void releaseSeckillQuota(String skuId, String userId, String eventKey) {
        String quotaKey = String.format(BizConsts.SECKILL_QUOTA_KEY, skuId);
        SeckillSkuEntity seckillSku = seckillSkuRepo.getOneById(Long.valueOf(skuId));
        cacheService.delete(eventKey);
        cacheService.removeMember(quotaKey, userId);
        if (seckillSku != null) {
            cacheService.removeMember(String.format(
                    BizConsts.SECKILL_SPU_QUOTA_KEY, seckillSku.getSeckillSpuId()), userId);
        }
        cacheService.increase(String.format(BizConsts.SECKILL_STOCK_KEY, skuId));
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
     * 扫描已到执行时间的退款任务，补偿 MQ 丢失、进程崩溃或微信回调丢失。
     */
    @Scheduled(fixedDelay = 60_000)
    public void reconcileRefunds() {
        // 同一任务下统一调度普通、秒杀和拼团退款；各订单类型仍由专属服务处理补偿。
        String lockToken = tryAcquireJobLock(ORDER_REFUND_RECONCILE_LOCK);
        if (lockToken == null) {
            return;
        }
        try {
            List<OrderRefundEntity> dueRefunds = orderRefundRepo.findDueList(LocalDateTime.now(), 100);
            for (OrderRefundEntity refund : dueRefunds) {
                try {
                    OrderEntity order = orderRepo.findOneById(refund.getOrderId());
                    if (order == null) {
                        log.error("退款任务缺少订单，refundId={}，orderId={}",
                                refund.getId(), refund.getOrderId());
                        String processingToken = refundDispatchService.claimDue(refund.getId());
                        if (processingToken != null) {
                            refundDispatchService.markFailed(
                                    refund.getId(), processingToken, "OrderNotFound");
                        }
                        continue;
                    }
                    if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
                        groupBuyTradeService.reconcileRefund(refund.getId());
                    } else {
                        orderService.reconcileRefund(refund.getId());
                    }
                } catch (Exception e) {
                    log.error("退款补偿任务处理失败，refundId={}, error={}",
                            refund.getId(), e.getClass().getSimpleName());
                }
            }
        } finally {
            releaseJobLock(ORDER_REFUND_RECONCILE_LOCK, lockToken);
        }
    }

    /**
     * 自动完成超过确认期限的已发货积分订单。
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000L)
    public void autoCompletePointsOrders() {
        executeWithJobLock(POINTS_ORDER_COMPLETE_LOCK, () -> {
            // 发货后自动完成天数由系统参数控制，平台可按运营策略动态调整。
            int days = pointsMallConfigService.getAutoCompleteDays();
            // 服务层使用条件更新，任务重入或多实例竞争时不会重复完成同一订单。
            int completed = adminPointsMallService.completeShippedOrdersBefore(LocalDateTime.now().minusDays(days));
            if (completed > 0) {
                log.info("自动完成积分订单数量：{}", completed);
            }
        });
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
