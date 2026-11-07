package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.common.enums.RefundDispatchStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.WxRefundStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.mapper.OrderRefundMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
public class OrderRefundRepo extends BaseRepo<OrderRefundMapper, OrderRefundEntity> {

    /**
     * createOne
     */
    public OrderRefundEntity createOne(OrderEntity order, String refundNo,
                                       BigDecimal totalAmount, BigDecimal refundAmount, String refundReason,
                                       LocalDateTime nextProcessTime) {
        OrderRefundEntity orderRefund = new OrderRefundEntity();
        orderRefund.setMchId(order.getMchId());
        orderRefund.setShopId(order.getShopId());
        orderRefund.setMemberId(order.getMemberId());
        orderRefund.setOrderId(order.getId());
        orderRefund.setOrderNo(order.getOrderNo());
        orderRefund.setRefundNo(refundNo);
        orderRefund.setTotalAmount(totalAmount);
        orderRefund.setRefundAmount(refundAmount);
        orderRefund.setRefundStatus(WxRefundStatusEnum.PROCESSING.getValue());
        orderRefund.setRefundReason(refundReason);
        orderRefund.setDispatchStatus(RefundDispatchStatusEnum.PENDING.getValue());
        orderRefund.setNextProcessTime(nextProcessTime);
        orderRefund.setRequestCount(0);
        orderRefund.setLastError("");
        orderRefund.setProcessingToken("");
        if (!orderRefund.insert()) {
            throw new IllegalStateException("创建退款任务失败");
        }
        log.debug("orderRefund.createOne: true");
        return orderRefund;
    }

    /**
     * findOneByRefundNo
     */
    public OrderRefundEntity findOneByRefundNo(String refundNo) {
        return lambdaQuery().eq(OrderRefundEntity::getRefundNo, refundNo).one();
    }

    /**
     * findListByOrderId
     */
    public List<OrderRefundEntity> findListByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderRefundEntity::getOrderId, orderId).list();
    }

    /**
     * 查询订单最新退款记录。
     */
    public OrderRefundEntity findLatestByOrderId(Long orderId) {
        List<OrderRefundEntity> refunds = lambdaQuery()
                .eq(OrderRefundEntity::getOrderId, orderId)
                .orderByDesc(OrderRefundEntity::getCreateTime)
                .list();
        return refunds.isEmpty() ? null : refunds.get(0);
    }

    /**
     * 同步微信退款结果，防止旧的处理中或失败结果覆盖已成功状态。
     */
    public boolean updateWxResult(Long refundId, String processingToken,
                                  String refundStatus, LocalDateTime successTime,
                                  String userReceivedAccount, String summary,
                                  Number realTotalAmount, Number realRefundAmount) {
        LambdaUpdateChainWrapper<OrderRefundEntity> update =
                this.lambdaUpdate().eq(OrderRefundEntity::getId, refundId);
        if (processingToken != null) {
            update.eq(OrderRefundEntity::getProcessingToken, processingToken)
                    .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue());
        }
        if (WxRefundStatusEnum.PROCESSING.equalsValue(refundStatus)) {
            update.notIn(OrderRefundEntity::getRefundStatus,
                    WxRefundStatusEnum.SUCCESS.getValue(),
                    WxRefundStatusEnum.CLOSED.getValue(),
                    WxRefundStatusEnum.ABNORMAL.getValue());
        } else if (WxRefundStatusEnum.CLOSED.equalsValue(refundStatus)
                || WxRefundStatusEnum.ABNORMAL.equalsValue(refundStatus)) {
            update.ne(OrderRefundEntity::getRefundStatus, WxRefundStatusEnum.SUCCESS.getValue());
        }
        return update.set(OrderRefundEntity::getRefundStatus, refundStatus)
                .set(successTime != null, OrderRefundEntity::getSuccessTime, successTime)
                .set(userReceivedAccount != null, OrderRefundEntity::getUserReceivedAccount, userReceivedAccount)
                .set(summary != null, OrderRefundEntity::getSummary, summary)
                .set(realTotalAmount != null, OrderRefundEntity::getRealTotalAmount,
                        this.toBigDecimal(realTotalAmount))
                .set(realRefundAmount != null, OrderRefundEntity::getRealRefundAmount,
                        this.toBigDecimal(realRefundAmount))
                .update();
    }

    /**
     * 查询已到处理时间的退款任务。
     */
    public List<OrderRefundEntity> findDueList(LocalDateTime now, int limit) {
        return this.lambdaQuery()
                .le(OrderRefundEntity::getNextProcessTime, now)
                .in(OrderRefundEntity::getDispatchStatus, Arrays.asList(
                        RefundDispatchStatusEnum.PENDING.getValue(),
                        RefundDispatchStatusEnum.PROCESSING.getValue(),
                        RefundDispatchStatusEnum.SUBMITTED.getValue(),
                        RefundDispatchStatusEnum.RETRY.getValue()))
                .orderByAsc(OrderRefundEntity::getNextProcessTime)
                .last("limit " + limit)
                .list();
    }

    /**
     * MQ 立即抢占尚未处理或等待重试的退款任务。
     */
    public boolean claimForMq(Long refundId, String processingToken, LocalDateTime leaseUntil) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                // MQ 只负责首次快速处理，RETRY 必须遵守 nextProcessTime 由 Job 执行。
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PENDING.getValue())
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .set(OrderRefundEntity::getProcessingToken, processingToken)
                .set(OrderRefundEntity::getNextProcessTime, leaseUntil)
                .update();
    }

    /**
     * Job 在到期后抢占待处理、待查询、待重试或已超时的处理中任务。
     */
    public boolean claimDue(Long refundId, String processingToken,
                            LocalDateTime now, LocalDateTime leaseUntil) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .le(OrderRefundEntity::getNextProcessTime, now)
                .in(OrderRefundEntity::getDispatchStatus, Arrays.asList(
                        RefundDispatchStatusEnum.PENDING.getValue(),
                        RefundDispatchStatusEnum.PROCESSING.getValue(),
                        RefundDispatchStatusEnum.SUBMITTED.getValue(),
                        RefundDispatchStatusEnum.RETRY.getValue()))
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .set(OrderRefundEntity::getProcessingToken, processingToken)
                .set(OrderRefundEntity::getNextProcessTime, leaseUntil)
                .update();
    }

    /**
     * 记录一次真实的微信退款申请调用。
     */
    public boolean startRequest(Long refundId, String processingToken, int maxRequestCount) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .eq(OrderRefundEntity::getProcessingToken, processingToken)
                .lt(OrderRefundEntity::getRequestCount, maxRequestCount)
                .setSql("request_count = request_count + 1")
                .update();
    }

    /**
     * 请求次数达到上限时停止自动重试。
     */
    public boolean markRequestLimitReached(Long refundId, String processingToken, int maxRequestCount,
                                           LocalDateTime nextProcessTime) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .eq(OrderRefundEntity::getProcessingToken, processingToken)
                .ge(OrderRefundEntity::getRequestCount, maxRequestCount)
                // 不再调用退款申请，但继续查询原退款单，避免未知结果被误判为失败。
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.SUBMITTED.getValue())
                .set(OrderRefundEntity::getNextProcessTime, nextProcessTime)
                .set(OrderRefundEntity::getLastError, "RequestLimitReached")
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    /**
     * 微信已接受申请，延迟查询最终结果。
     */
    public boolean markSubmitted(Long refundId, String processingToken, LocalDateTime nextProcessTime) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .eq(OrderRefundEntity::getProcessingToken, processingToken)
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.SUBMITTED.getValue())
                .set(OrderRefundEntity::getNextProcessTime, nextProcessTime)
                .set(OrderRefundEntity::getLastError, "")
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    /**
     * 当前处理失败，延迟到下一轮 Job 重试。
     */
    public boolean markRetry(Long refundId, String processingToken,
                             LocalDateTime nextProcessTime, String errorType) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .eq(OrderRefundEntity::getProcessingToken, processingToken)
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.RETRY.getValue())
                .set(OrderRefundEntity::getNextProcessTime, nextProcessTime)
                .set(OrderRefundEntity::getLastError, errorType)
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    /**
     * 微信仍在退款中，继续延迟查询，不覆盖已完成状态。
     */
    public boolean markWxProcessing(Long refundId, String processingToken,
                                    LocalDateTime nextProcessTime) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .eq(OrderRefundEntity::getProcessingToken, processingToken)
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.SUBMITTED.getValue())
                .set(OrderRefundEntity::getNextProcessTime, nextProcessTime)
                .set(OrderRefundEntity::getLastError, "")
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    /**
     * 微信处理中回调不依赖任务租约，但不能覆盖已结束任务。
     */
    public boolean markWxProcessingFromCallback(Long refundId, LocalDateTime nextProcessTime) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .ne(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.COMPLETED.getValue())
                .ne(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.FAILED.getValue())
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.SUBMITTED.getValue())
                .set(OrderRefundEntity::getNextProcessTime, nextProcessTime)
                .set(OrderRefundEntity::getLastError, "")
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    /**
     * 微信退款进入成功、关闭或异常终态。
     */
    public boolean markCompleted(Long refundId) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.COMPLETED.getValue())
                .set(OrderRefundEntity::getNextProcessTime, null)
                .set(OrderRefundEntity::getLastError, "")
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    /**
     * 当前任务持有者根据微信终态结束任务。
     */
    public boolean markCompleted(Long refundId, String processingToken) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .eq(OrderRefundEntity::getProcessingToken, processingToken)
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.COMPLETED.getValue())
                .set(OrderRefundEntity::getNextProcessTime, null)
                .set(OrderRefundEntity::getLastError, "")
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    /**
     * 数据不完整等无法自动恢复的场景停止任务。
     */
    public boolean markFailed(Long refundId, String processingToken, String errorType) {
        return this.lambdaUpdate()
                .eq(OrderRefundEntity::getId, refundId)
                .eq(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.PROCESSING.getValue())
                .eq(OrderRefundEntity::getProcessingToken, processingToken)
                .set(OrderRefundEntity::getDispatchStatus, RefundDispatchStatusEnum.FAILED.getValue())
                .set(OrderRefundEntity::getNextProcessTime, null)
                .set(OrderRefundEntity::getLastError, errorType)
                .set(OrderRefundEntity::getProcessingToken, "")
                .update();
    }

    private BigDecimal toBigDecimal(Number value) {
        return value == null ? null : new BigDecimal(String.valueOf(value));
    }
}
