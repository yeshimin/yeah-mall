package com.yeshimin.yeahboot.service;

import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.WxRefundStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.repository.OrderRefundRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 退款请求的 MQ 快速处理与 Job 补偿调度。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundDispatchService {

    private static final long INITIAL_JOB_DELAY_SECONDS = 30L;
    private static final long RETRY_DELAY_MINUTES = 1L;
    private static final long PROCESSING_LEASE_MINUTES = 2L;
    private static final int MAX_REQUEST_COUNT = 10;

    private final IdService idService;
    private final OrderRepo orderRepo;
    private final OrderRefundRepo orderRefundRepo;
    private final WxPayService wxPayService;
    private final MqPublisher mqPublisher;

    /**
     * 在当前业务事务内创建可持久化的退款任务。
     */
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public OrderRefundEntity createTask(OrderEntity order, BigDecimal totalAmount,
                                        BigDecimal refundAmount, String refundReason) {
        String refundNo = idService.nextEncodedId();
        return orderRefundRepo.createOne(
                order, refundNo, totalAmount, refundAmount, refundReason,
                LocalDateTime.now().plusSeconds(INITIAL_JOB_DELAY_SECONDS));
    }

    /**
     * 业务事务提交后发布快速处理消息；发布失败由延迟 Job 补偿。
     */
    public void publishSafely(Long refundId) {
        if (refundId == null) {
            return;
        }
        try {
            MqMessage message = new MqMessage();
            message.setTopic(BizConsts.ORDER_REFUND_TOPIC);
            message.setPayload(String.valueOf(refundId));
            mqPublisher.publish(message);
        } catch (Exception e) {
            log.warn("发布退款处理消息失败，等待补偿任务，refundId={}，error={}",
                    refundId, this.errorType(e));
        }
    }

    /**
     * MQ 消费后尝试立即调用微信退款。
     */
    public void processMq(Long refundId) {
        OrderRefundEntity refund = orderRefundRepo.findOneById(refundId);
        String processingToken = this.claimForMq(refundId);
        if (refund == null || processingToken == null) {
            return;
        }
        OrderEntity order = orderRepo.findOneById(refund.getOrderId());
        if (order == null) {
            this.markFailed(refundId, processingToken, "OrderNotFound");
            return;
        }
        this.requestClaimedRefund(order, refund, processingToken);
    }

    /**
     * Job 按下次处理时间原子抢占退款任务。
     */
    public String claimDue(Long refundId) {
        LocalDateTime now = LocalDateTime.now();
        String processingToken = UUID.randomUUID().toString();
        return orderRefundRepo.claimDue(
                refundId, processingToken, now, now.plusMinutes(PROCESSING_LEASE_MINUTES))
                ? processingToken : null;
    }

    /**
     * Job 查询微信失败时，使用稳定退款单号重试申请。
     */
    public void retryClaimed(OrderEntity order, OrderRefundEntity refund, String processingToken) {
        this.requestClaimedRefund(order, refund, processingToken);
    }

    /**
     * 根据微信回调或主动查询结果安排后续调度。
     */
    public void markFromWxCallback(Long refundId, String wxStatus) {
        if (WxRefundStatusEnum.SUCCESS.equalsValue(wxStatus)
                || WxRefundStatusEnum.CLOSED.equalsValue(wxStatus)
                || WxRefundStatusEnum.ABNORMAL.equalsValue(wxStatus)) {
            orderRefundRepo.markCompleted(refundId);
            return;
        }
        if (WxRefundStatusEnum.PROCESSING.equalsValue(wxStatus)) {
            orderRefundRepo.markWxProcessingFromCallback(
                    refundId, LocalDateTime.now().plusMinutes(RETRY_DELAY_MINUTES));
            return;
        }
        log.warn("收到未知微信退款回调状态，refundId={}，status={}", refundId, wxStatus);
    }

    /**
     * 当前Job持有者根据主动查询结果安排后续调度。
     */
    public void markFromWxQuery(Long refundId, String processingToken, String wxStatus) {
        if (WxRefundStatusEnum.SUCCESS.equalsValue(wxStatus)
                || WxRefundStatusEnum.CLOSED.equalsValue(wxStatus)
                || WxRefundStatusEnum.ABNORMAL.equalsValue(wxStatus)) {
            orderRefundRepo.markCompleted(refundId, processingToken);
            return;
        }
        if (WxRefundStatusEnum.PROCESSING.equalsValue(wxStatus)) {
            orderRefundRepo.markWxProcessing(
                    refundId, processingToken, LocalDateTime.now().plusMinutes(RETRY_DELAY_MINUTES));
            return;
        }
        this.markRetry(refundId, processingToken, "UnknownWxRefundStatus");
    }

    /**
     * 本地同步失败时释放任务，等待下轮 Job 再次查询。
     */
    public void markRetry(Long refundId, String processingToken, Throwable error) {
        this.markRetry(refundId, processingToken, this.errorType(error));
    }

    /**
     * 数据缺失等无法自动恢复时停止任务，避免持续占用 Job 扫描批次。
     */
    public void markFailed(Long refundId, String processingToken, String errorType) {
        OrderRefundEntity refund = orderRefundRepo.findOneById(refundId);
        if (refund == null || !orderRefundRepo.markFailed(refundId, processingToken, errorType)) {
            return;
        }
        OrderEntity order = orderRepo.findOneById(refund.getOrderId());
        if (order == null) {
            return;
        }
        if (refund.getRequestCount() == null || refund.getRequestCount() == 0) {
            // 从未调用微信时可安全失败，商家可修复数据后重新处理。
            orderRepo.updateProcessingRefundStatus(
                    order.getId(), RefundStatusEnum.FAILED.getValue(), errorType, LocalDateTime.now());
        } else {
            // 已调用微信时结果未知，不能标记失败后再创建新退款单。
            orderRepo.updateProcessingRefundSummary(
                    order.getId(), "退款任务数据异常，请核对微信退款结果");
        }
    }

    private String claimForMq(Long refundId) {
        String processingToken = UUID.randomUUID().toString();
        return orderRefundRepo.claimForMq(
                refundId, processingToken, LocalDateTime.now().plusMinutes(PROCESSING_LEASE_MINUTES))
                ? processingToken : null;
    }

    private void requestClaimedRefund(OrderEntity order, OrderRefundEntity refund, String processingToken) {
        if (!orderRefundRepo.startRequest(refund.getId(), processingToken, MAX_REQUEST_COUNT)) {
            if (orderRefundRepo.markRequestLimitReached(
                    refund.getId(), processingToken, MAX_REQUEST_COUNT,
                    LocalDateTime.now().plusMinutes(RETRY_DELAY_MINUTES))) {
                log.error("退款请求已达自动调用上限，后续仅查询原退款单，orderId={}，refundNo={}",
                        order.getId(), refund.getRefundNo());
            }
            return;
        }
        try {
            wxPayService.refund(order, refund.getRefundNo(), refund.getRefundReason(),
                    this.toFen(refund.getRefundAmount()), this.toFen(refund.getTotalAmount()));
            this.markSubmitted(refund.getId(), processingToken);
        } catch (Exception e) {
            this.markRetry(refund.getId(), processingToken, this.errorType(e));
            log.warn("调用微信退款接口失败，orderId={}，refundNo={}，error={}",
                    order.getId(), refund.getRefundNo(), this.errorType(e));
        }
    }

    private void markSubmitted(Long refundId, String processingToken) {
        orderRefundRepo.markSubmitted(
                refundId, processingToken, LocalDateTime.now().plusMinutes(RETRY_DELAY_MINUTES));
    }

    private void markRetry(Long refundId, String processingToken, String errorType) {
        orderRefundRepo.markRetry(
                refundId, processingToken, LocalDateTime.now().plusMinutes(RETRY_DELAY_MINUTES), errorType);
    }

    private long toFen(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("退款金额无效");
        }
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private String errorType(Throwable error) {
        String type = error == null ? "UnknownError" : error.getClass().getSimpleName();
        return type.length() <= 64 ? type : type.substring(0, 64);
    }
}
