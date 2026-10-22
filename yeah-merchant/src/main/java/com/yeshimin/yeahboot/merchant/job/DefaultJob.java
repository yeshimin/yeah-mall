package com.yeshimin.yeahboot.merchant.job;

import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.domain.entity.OrderDeliveryTrackingEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.repository.OrderDeliveryTrackingRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.merchant.service.MchOrderService;
import com.yeshimin.yeahboot.service.JuheExpService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultJob {

    private static final String DELIVERY_TRACKING_SYNC_LOCK = "yeah:job:delivery:tracking-sync";
    private static final String ORDER_PAY_EXPIRE_CANCEL_LOCK = "yeah:job:order:pay-expire-cancel";
    private static final String ORDER_RECEIVE_EXPIRE_LOCK = "yeah:job:order:receive-expire";
    private static final long JOB_LOCK_LEASE_MILLIS = 10 * 60 * 1000L;
    private static final String TRY_LOCK_LUA =
            "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2]) then return 1 else return 0 end";
    private static final String RELEASE_LOCK_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private final OrderDeliveryTrackingRepo orderDeliveryTrackingRepo;
    private final OrderRepo orderRepo;

    private final CacheService cacheService;
    private final JuheExpService juheExpService;

    private final MchOrderService mchOrderService;

    /**
     * 同步物流跟踪信息
     * 每半小时执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0/30 * * * ?")
    public void syncDeliveryTracking() {
        executeWithJobLock(DELIVERY_TRACKING_SYNC_LOCK, this::syncDeliveryTrackingInternal);
    }

    /**
     * 同步物流跟踪信息。
     */
    private void syncDeliveryTrackingInternal() {
        log.info("同步物流跟踪信息-开始");

        // 获取待查询的数据列表；【后期按需优化】TODO
        List<OrderDeliveryTrackingEntity> listTracking = orderDeliveryTrackingRepo.findListForQuery();

        for (OrderDeliveryTrackingEntity tracking : listTracking) {
            LocalDateTime now = LocalDateTime.now();

            // 调用聚合数据平台接口查询快递信息
            JSONObject jsonResp = null;
            try {
                jsonResp = juheExpService.queryExpress(tracking.getTrackingCom(), tracking.getTrackingNo(),
                        tracking.getSenderPhone(), tracking.getReceiverPhone());
            } catch (Exception e) {
                log.error("查询快递信息失败, trackingNo: {}", tracking.getTrackingNo(), e);
            }

            // 不管成功与否，记录响应数据和查询时间
            tracking.setLastRespData(jsonResp != null ? jsonResp.toJSONString() : "");
            tracking.setLastQueryTime(now);

            // 如果查询成功，记录成功响应数据和查询时间
            if (jsonResp != null && Objects.equals(jsonResp.getString("error_code"), "0")) {
                tracking.setLastSuccessRespData(jsonResp.toJSONString());
                tracking.setLastSuccessQueryTime(now);

                JSONObject result = jsonResp.getJSONObject("result");
                tracking.setTrackingStatus(result.getString("status_detail"));

                // 判断是否锁定状态
                String status = result.getString("status");
                if (Objects.equals(status, "1")) {
                    tracking.setStatusLocked(true);
                }
            } else {
                log.warn("查询快递信息失败, orderNo: {}, trackingNo: {}, response: {}",
                        tracking.getOrderNo(), tracking.getTrackingNo(),
                        jsonResp != null ? jsonResp.toJSONString() : "");
            }
        }

        // 批量更新
        orderDeliveryTrackingRepo.updateBatchById(listTracking);

        log.info("同步物流跟踪信息-结束");
    }

    /**
     * 待付款订单超时取消
     * 每分钟执行一次
     */
    @SneakyThrows
    @Scheduled(fixedDelay = 60000)
    public void autoCancelOrder() {
        executeWithJobLock(ORDER_PAY_EXPIRE_CANCEL_LOCK, this::autoCancelOrderInternal);
    }

    /**
     * 取消支付超时订单。
     */
    private void autoCancelOrderInternal() {
        log.info("待付款订单超时取消-开始");

        // 查询待处理的订单列表
        List<Long> orderIds = orderRepo.findIdListForPayExpired()
                .stream().map(OrderEntity::getId).collect(Collectors.toList());

        for (Long orderId : orderIds) {
            try {
                // 取消超时订单
                mchOrderService.cancelExpiredOrder(orderId, "支付超时自动取消");
            } catch (Exception e) {
                log.error("取消超时未付款订单失败，订单ID：{}", orderId, e);
            }
        }

        log.info("待付款订单超时取消-结束");
    }

    /**
     * 待收货订单签收超时的自动确认收货
     * 每天1点执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoReceiveOrder() {
        executeWithJobLock(ORDER_RECEIVE_EXPIRE_LOCK, this::autoReceiveOrderInternal);
    }

    /**
     * 自动确认签收超时订单。
     */
    private void autoReceiveOrderInternal() {
        log.info("待收货订单签收超时的自动确认收货-开始");

        // 查询待处理的订单列表
        List<Long> orderIds = orderRepo.findIdListForReceiveExpired()
                .stream().map(OrderEntity::getId).collect(Collectors.toList());

        for (Long orderId : orderIds) {
            try {
                // 自动签收
                mchOrderService.confirmReceive(orderId, "签收超时自动确认");
            } catch (Exception e) {
                log.error("待收货订单自动签收失败，订单ID：{}", orderId, e);
            }
        }

        log.info("待收货订单签收超时的自动确认收货-结束");
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
     * 仅释放当前实例持有的锁，避免误删锁过期后被其他实例获取的锁。
     */
    private void releaseJobLock(String lockKey, String lockToken) {
        cacheService.executeLua(RELEASE_LOCK_LUA, java.util.Collections.singletonList(lockKey),
                java.util.Collections.singletonList(lockToken));
    }
}
