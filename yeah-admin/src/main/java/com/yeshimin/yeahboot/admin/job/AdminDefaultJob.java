package com.yeshimin.yeahboot.admin.job;

import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillOrderCancelMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.SeckillEventCacheVo;
import com.yeshimin.yeahboot.basic.service.storage.StorageService;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.properties.SeckillProperties;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.SysStorageRepo;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqPublisher;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDefaultJob {

    private final SysStorageRepo sysStorageRepo;

    private final StorageService storageService;
    private final CacheService cacheService;
    private final MqPublisher mqPublisher;

    private final SeckillProperties seckillProperties;

    /**
     * 清理过期未使用的存储记录
     * 每天1点执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoReceiveOrder() {
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
}
