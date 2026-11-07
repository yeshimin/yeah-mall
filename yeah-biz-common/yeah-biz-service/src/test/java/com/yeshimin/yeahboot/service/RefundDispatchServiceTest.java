package com.yeshimin.yeahboot.service;

import com.wechat.pay.java.service.refund.model.Refund;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefundDispatchServiceTest {

    private IdService idService;
    private OrderRepo orderRepo;
    private OrderRefundRepo orderRefundRepo;
    private WxPayService wxPayService;
    private MqPublisher mqPublisher;
    private RefundDispatchService service;

    @BeforeEach
    void setUp() {
        idService = mock(IdService.class);
        orderRepo = mock(OrderRepo.class);
        orderRefundRepo = mock(OrderRefundRepo.class);
        wxPayService = mock(WxPayService.class);
        mqPublisher = mock(MqPublisher.class);
        service = new RefundDispatchService(idService, orderRepo, orderRefundRepo, wxPayService, mqPublisher);
    }

    @Test
    void createsDurableTaskWithDelayedJobTime() {
        OrderEntity order = order(1L);
        OrderRefundEntity created = refund(10L, order.getId());
        when(idService.nextEncodedId()).thenReturn("refund-no");
        when(orderRefundRepo.createOne(eq(order), eq("refund-no"), any(BigDecimal.class),
                any(BigDecimal.class), eq("退款原因"), any(LocalDateTime.class))).thenReturn(created);
        LocalDateTime before = LocalDateTime.now().plusSeconds(29);

        OrderRefundEntity result = service.createTask(
                order, new BigDecimal("0.01"), new BigDecimal("0.01"), "退款原因");

        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(orderRefundRepo).createOne(eq(order), eq("refund-no"), any(BigDecimal.class),
                any(BigDecimal.class), eq("退款原因"), timeCaptor.capture());
        assertEquals(created, result);
        assertTrue(!timeCaptor.getValue().isBefore(before));
    }

    @Test
    void publishesOnlyRefundTaskId() {
        service.publishSafely(10L);

        ArgumentCaptor<MqMessage> messageCaptor = ArgumentCaptor.forClass(MqMessage.class);
        verify(mqPublisher).publish(messageCaptor.capture());
        assertEquals(BizConsts.ORDER_REFUND_TOPIC, messageCaptor.getValue().getTopic());
        assertEquals("10", messageCaptor.getValue().getPayload());
    }

    @Test
    void publishFailureDoesNotBreakCommittedBusiness() {
        org.mockito.Mockito.doThrow(new IllegalStateException("redis unavailable"))
                .when(mqPublisher).publish(any(MqMessage.class));

        assertDoesNotThrow(() -> service.publishSafely(10L));
    }

    @Test
    void mqClaimsAndSubmitsRefund() {
        OrderRefundEntity refund = refund(10L, 1L);
        OrderEntity order = order(1L);
        AtomicReference<String> processingToken = new AtomicReference<>();
        when(orderRefundRepo.findOneById(10L)).thenReturn(refund);
        when(orderRefundRepo.claimForMq(eq(10L), anyString(), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    processingToken.set(invocation.getArgument(1));
                    return true;
                });
        when(orderRepo.findOneById(1L)).thenReturn(order);
        when(orderRefundRepo.startRequest(eq(10L), anyString(), eq(10)))
                .thenAnswer(invocation -> processingToken.get().equals(invocation.getArgument(1)));
        when(wxPayService.refund(order, "refund-no", "reason", 1L, 1L)).thenReturn(mock(Refund.class));

        service.processMq(10L);

        verify(wxPayService).refund(order, "refund-no", "reason", 1L, 1L);
        verify(orderRefundRepo).markSubmitted(
                eq(10L), eq(processingToken.get()), any(LocalDateTime.class));
        verify(orderRefundRepo, never()).markRetry(
                anyLong(), anyString(), any(LocalDateTime.class), any(String.class));
    }

    @Test
    void mqAndJobRaceOnlyLetsClaimWinnerCallWechat() {
        OrderRefundEntity refund = refund(10L, 1L);
        when(orderRefundRepo.findOneById(10L)).thenReturn(refund);
        when(orderRefundRepo.claimForMq(eq(10L), anyString(), any(LocalDateTime.class))).thenReturn(false);

        service.processMq(10L);

        verify(wxPayService, never()).refund(any(OrderEntity.class), any(String.class), any(String.class),
                anyLong(), anyLong());
    }

    @Test
    void failedWechatRequestIsDelayedForJobRetry() {
        OrderRefundEntity refund = refund(10L, 1L);
        OrderEntity order = order(1L);
        when(orderRefundRepo.findOneById(10L)).thenReturn(refund);
        when(orderRefundRepo.claimForMq(eq(10L), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(orderRepo.findOneById(1L)).thenReturn(order);
        when(orderRefundRepo.startRequest(eq(10L), anyString(), eq(10))).thenReturn(true);
        when(wxPayService.refund(order, "refund-no", "reason", 1L, 1L))
                .thenThrow(new IllegalStateException("sensitive upstream response"));

        assertDoesNotThrow(() -> service.processMq(10L));

        verify(orderRefundRepo).markRetry(
                eq(10L), anyString(), any(LocalDateTime.class), eq("IllegalStateException"));
    }

    @Test
    void terminalWechatStatusCompletesTask() {
        service.markFromWxCallback(10L, WxRefundStatusEnum.SUCCESS.getValue());

        verify(orderRefundRepo).markCompleted(10L);
        verify(orderRefundRepo, never()).markCompleted(eq(10L), anyString());
    }

    @Test
    void staleQueryWorkerCanOnlyCompleteWithItsProcessingToken() {
        service.markFromWxQuery(10L, "old-token", WxRefundStatusEnum.SUCCESS.getValue());

        verify(orderRefundRepo).markCompleted(10L, "old-token");
        verify(orderRefundRepo, never()).markCompleted(10L);
    }

    @Test
    void requestLimitStopsNewRequestsButKeepsQueryingOriginalRefund() {
        OrderRefundEntity refund = refund(10L, 1L);
        OrderEntity order = order(1L);
        when(orderRefundRepo.findOneById(10L)).thenReturn(refund);
        when(orderRefundRepo.claimForMq(eq(10L), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(orderRepo.findOneById(1L)).thenReturn(order);
        when(orderRefundRepo.startRequest(eq(10L), anyString(), eq(10))).thenReturn(false);
        when(orderRefundRepo.markRequestLimitReached(
                eq(10L), anyString(), eq(10), any(LocalDateTime.class))).thenReturn(true);

        service.processMq(10L);

        verify(wxPayService, never()).refund(any(OrderEntity.class), any(String.class), any(String.class),
                anyLong(), anyLong());
        verify(orderRefundRepo).markRequestLimitReached(
                eq(10L), anyString(), eq(10), any(LocalDateTime.class));
    }

    @Test
    void orphanTaskWithoutWechatRequestCanSafelyFailOrder() {
        OrderRefundEntity refund = refund(10L, 1L);
        refund.setRequestCount(0);
        OrderEntity order = order(1L);
        when(orderRefundRepo.findOneById(10L)).thenReturn(refund);
        when(orderRefundRepo.markFailed(10L, "token", "GroupBuyOrderNotFound")).thenReturn(true);
        when(orderRepo.findOneById(1L)).thenReturn(order);

        service.markFailed(10L, "token", "GroupBuyOrderNotFound");

        verify(orderRepo).updateProcessingRefundStatus(
                eq(1L), eq(RefundStatusEnum.FAILED.getValue()),
                eq("GroupBuyOrderNotFound"), any(LocalDateTime.class));
    }

    @Test
    void orphanTaskAfterWechatRequestRequiresManualReview() {
        OrderRefundEntity refund = refund(10L, 1L);
        refund.setRequestCount(1);
        OrderEntity order = order(1L);
        when(orderRefundRepo.findOneById(10L)).thenReturn(refund);
        when(orderRefundRepo.markFailed(10L, "token", "GroupBuyOrderNotFound")).thenReturn(true);
        when(orderRepo.findOneById(1L)).thenReturn(order);

        service.markFailed(10L, "token", "GroupBuyOrderNotFound");

        verify(orderRepo).updateProcessingRefundSummary(eq(1L), any(String.class));
        verify(orderRepo, never()).updateProcessingRefundStatus(
                anyLong(), any(String.class), any(String.class), any(LocalDateTime.class));
    }

    @Test
    void orphanTaskCanOnlyBeStoppedByCurrentLeaseOwner() {
        OrderRefundEntity refund = refund(10L, 1L);
        refund.setRequestCount(0);
        when(orderRefundRepo.findOneById(10L)).thenReturn(refund);
        when(orderRefundRepo.markFailed(10L, "old-token", "OrderNotFound")).thenReturn(false);

        service.markFailed(10L, "old-token", "OrderNotFound");

        verify(orderRepo, never()).findOneById(anyLong());
        verify(orderRepo, never()).updateProcessingRefundStatus(
                anyLong(), any(String.class), any(String.class), any(LocalDateTime.class));
    }

    private OrderEntity order(Long id) {
        OrderEntity order = new OrderEntity();
        order.setId(id);
        return order;
    }

    private OrderRefundEntity refund(Long id, Long orderId) {
        OrderRefundEntity refund = new OrderRefundEntity();
        refund.setId(id);
        refund.setOrderId(orderId);
        refund.setRefundNo("refund-no");
        refund.setRefundReason("reason");
        refund.setTotalAmount(new BigDecimal("0.01"));
        refund.setRefundAmount(new BigDecimal("0.01"));
        return refund;
    }
}
