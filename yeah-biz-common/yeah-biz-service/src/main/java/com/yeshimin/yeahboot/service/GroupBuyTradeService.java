package com.yeshimin.yeahboot.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyGroupStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyOrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.WxRefundStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyGroupEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.repository.GroupBuyGroupRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyOrderRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySkuRepo;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import com.yeshimin.yeahboot.data.repository.OrderCouponRepo;
import com.yeshimin.yeahboot.data.repository.OrderRefundRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 拼团交易状态服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupBuyTradeService {

    private final GroupBuyOrderRepo groupBuyOrderRepo;
    private final GroupBuyGroupRepo groupBuyGroupRepo;
    private final GroupBuySkuRepo groupBuySkuRepo;
    private final ProductSkuRepo productSkuRepo;
    private final OrderRepo orderRepo;
    private final OrderRefundRepo orderRefundRepo;
    private final OrderCouponRepo orderCouponRepo;
    private final MemberCouponRepo memberCouponRepo;
    private final MchCouponRepo mchCouponRepo;
    private final WxPayService wxPayService;
    private final OrderService orderService;
    private final RefundDispatchService refundDispatchService;
    private final TransactionTemplate transactionTemplate;
    private final PlatformTransactionManager transactionManager;

    /**
     * 处理拼团订单支付成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderPaid(Long orderId) {
        GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getOrderId, orderId).one();
        if (groupBuyOrder == null || !GroupBuyOrderStatusEnum.WAIT_PAY.equalsValue(groupBuyOrder.getStatus())) {
            return;
        }

        // 回调可能重复投递，先用状态更新确保每个参与订单只计入一次人数。
        boolean paid = groupBuyOrderRepo.lambdaUpdate()
                .eq(GroupBuyOrderEntity::getId, groupBuyOrder.getId())
                .eq(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.WAIT_PAY.getValue())
                .set(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.PAID.getValue())
                .update();
        if (!paid) {
            return;
        }

        // 锁住团记录后再处理成员数，避免支付回调与活动结算并发修改团状态。
        GroupBuyGroupEntity group = groupBuyGroupRepo.findOneByIdForUpdate(groupBuyOrder.getGroupId());
        if (group == null) {
            throw new BaseException("拼团记录不存在");
        }
        if (GroupBuyGroupStatusEnum.WAIT_PAY.equalsValue(group.getStatus())) {
            if (!Boolean.TRUE.equals(groupBuyOrder.getIsLeader())) {
                throw new BaseException("拼团状态异常");
            }
            boolean forming = groupBuyGroupRepo.lambdaUpdate()
                    .eq(GroupBuyGroupEntity::getId, group.getId())
                    .eq(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.WAIT_PAY.getValue())
                    .set(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.FORMING.getValue())
                    .set(GroupBuyGroupEntity::getMemberCount, 1)
                    .set(GroupBuyGroupEntity::getReservedMemberCount, 1)
                    .update();
            if (!forming) {
                return;
            }
        } else if (GroupBuyGroupStatusEnum.FORMING.equalsValue(group.getStatus())) {
            // 成员数条件更新同时承担“每团人数上限”的最终并发校验。
            boolean increased = groupBuyGroupRepo.increaseMemberCountIfForming(
                    group.getId(), GroupBuyGroupStatusEnum.FORMING.getValue());
            if (!increased) {
                // 团状态尚未来得及切换但人数已满时，迟到支付不能占用额外席位。
                this.refundFailedGroupOrder(groupBuyOrder);
                return;
            }
        } else if (GroupBuyGroupStatusEnum.SUCCESS.equalsValue(group.getStatus())) {
            // 团已成功说明名额已满；迟到支付订单必须退款，不能绕过每团人数上限。
            this.refundFailedGroupOrder(groupBuyOrder);
            return;
        } else {
            // 团已失败的迟到支付回调同样必须退款，不能让订单停留在待成团状态。
            this.refundFailedGroupOrder(groupBuyOrder);
            return;
        }

        // 每次成功计数后尝试成团；状态条件更新保证只有一个回调能完成状态切换。
        this.tryCompleteGroup(group.getId());
    }

    /**
     * 处理未支付拼团订单关闭
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderClosed(Long orderId) {
        GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getOrderId, orderId).one();
        if (groupBuyOrder == null || !GroupBuyOrderStatusEnum.WAIT_PAY.equalsValue(groupBuyOrder.getStatus())) {
            return;
        }
        groupBuyOrderRepo.lambdaUpdate()
                .eq(GroupBuyOrderEntity::getId, groupBuyOrder.getId())
                .eq(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.WAIT_PAY.getValue())
                .set(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.CLOSED.getValue())
                .update();

        // 团长未支付时整个团尚未对外开放，直接关闭该团。
        if (Boolean.TRUE.equals(groupBuyOrder.getIsLeader())) {
            groupBuyGroupRepo.lambdaUpdate()
                    .eq(GroupBuyGroupEntity::getId, groupBuyOrder.getGroupId())
                    .eq(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.WAIT_PAY.getValue())
                    .set(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.CLOSED.getValue())
                    .set(GroupBuyGroupEntity::getReservedMemberCount, 0)
                    .update();
            return;
        }

        // 非团长待支付订单关闭后，释放其占用的席位，使其他用户可以继续参团。
        groupBuyGroupRepo.decreaseReservedMemberCountIfForming(
                groupBuyOrder.getGroupId(), GroupBuyGroupStatusEnum.FORMING.getValue());
    }

    /**
     * 处理拼团退款成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundSuccess(String orderNo) {
        GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getOrderNo, orderNo).one();
        if (groupBuyOrder == null) {
            return;
        }
        boolean refunded = groupBuyOrderRepo.lambdaUpdate()
                .eq(GroupBuyOrderEntity::getId, groupBuyOrder.getId())
                .eq(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.REFUNDING.getValue())
                .set(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.REFUNDED.getValue())
                .update();
        if (!refunded) {
            return;
        }

        OrderEntity order = orderRepo.findOneById(groupBuyOrder.getOrderId());
        // 已发货或已签收的仅退款不回补库存，避免用户保留商品时产生虚增库存。
        if (order == null || order.getShipTime() != null) {
            return;
        }

        // 退款回调可能重复投递，只有首次从退款中切为已退款时才回补库存。
        GroupBuySkuEntity groupBuySku = groupBuySkuRepo.findOneById(groupBuyOrder.getGroupBuySkuId());
        if (groupBuySku == null) {
            return;
        }
        // 活动结算前回补活动库存；结算后活动库存已释放，直接回补源SKU。
        if (Boolean.TRUE.equals(groupBuySku.getIsStockReserved())) {
            groupBuySkuRepo.increaseStock(groupBuySku.getId(), 1);
        } else {
            productSkuRepo.increaseStock(groupBuySku.getSkuId(), 1);
        }
    }

    /**
     * 结算活动内所有团：满团订单进入待发货，未满团关闭或退款。
     */
    public void settleActivity(Long activityId) {
        List<Long> groupIds = groupBuyGroupRepo.lambdaQuery()
                .eq(GroupBuyGroupEntity::getActivityId, activityId)
                .in(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.WAIT_PAY.getValue(),
                        GroupBuyGroupStatusEnum.FORMING.getValue(), GroupBuyGroupStatusEnum.SUCCESS.getValue())
                .list().stream()
                .map(GroupBuyGroupEntity::getId)
                .collect(Collectors.toList());
        TransactionTemplate settlementTransaction = new TransactionTemplate(transactionManager);
        settlementTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 团逐个锁定结算，避免同一活动内一个团的并发变化影响其他团。
        for (Long groupId : groupIds) {
            List<Long> refundIds = settlementTransaction.execute(status -> this.settleGroup(groupId));
            if (refundIds != null) {
                // 团结算事务提交后再发布 MQ，任何发布失败都由 Job 延迟补偿。
                refundIds.forEach(refundDispatchService::publishSafely);
            }
        }
        // 关单和查单属于远程调用，必须在团状态事务提交后执行。
        this.closeUnpaidActivityOrders(activityId);
    }

    /**
     * 查询并同步拼团自动退款状态，避免回调丢失导致退款长期处理中。
     */
    public void reconcileRefund(Long refundId) {
        OrderRefundEntity refund = orderRefundRepo.findOneById(refundId);
        if (refund == null) {
            return;
        }
        String processingToken = refundDispatchService.claimDue(refund.getId());
        if (processingToken == null) {
            return;
        }
        GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getOrderId, refund.getOrderId())
                .one();
        OrderEntity order = orderRepo.findOneById(refund.getOrderId());
        if (groupBuyOrder == null) {
            refundDispatchService.markFailed(refund.getId(), processingToken, "GroupBuyOrderNotFound");
            return;
        }
        if (order == null) {
            refundDispatchService.markFailed(refund.getId(), processingToken, "OrderNotFound");
            return;
        }
        Refund result;
        try {
            result = wxPayService.queryRefundByOutRefundNo(order, refund.getRefundNo());
        } catch (Exception e) {
            log.warn("查询拼团退款状态失败，refundNo={}, error={}",
                    refund.getRefundNo(), e.getClass().getSimpleName());
            refundDispatchService.retryClaimed(order, refund, processingToken);
            return;
        }
        try {
            transactionTemplate.executeWithoutResult(status ->
                    this.syncRefundQueryResult(groupBuyOrder, refund, result, processingToken));
            refundDispatchService.markFromWxQuery(refund.getId(), processingToken,
                    result == null || result.getStatus() == null ? null : result.getStatus().name());
        } catch (Exception e) {
            log.error("同步拼团退款查询结果失败，refundNo={}, error={}",
                    refund.getRefundNo(), e.getClass().getSimpleName());
            refundDispatchService.markRetry(refund.getId(), processingToken, e);
        }
    }

    /**
     * 将微信退款查询结果同步到退款单、主订单和拼团参与记录。
     */
    private void syncRefundQueryResult(GroupBuyOrderEntity groupBuyOrder, OrderRefundEntity orderRefund,
                                       Refund refund, String processingToken) {
        if (refund == null || refund.getStatus() == null) {
            return;
        }
        String refundStatus = refund.getStatus().name();
        LocalDateTime successTime = this.parseRefundSuccessTime(refund.getSuccessTime());
        // 先同步退款单的第三方状态，再更新业务订单和拼团参与状态。
        boolean refundResultUpdated = orderRefundRepo.updateWxResult(
                orderRefund.getId(), processingToken, refundStatus, successTime,
                refund.getUserReceivedAccount(), null,
                refund.getAmount() == null ? null : refund.getAmount().getTotal(),
                refund.getAmount() == null ? null : refund.getAmount().getRefund());
        if (!refundResultUpdated) {
            return;
        }

        OrderEntity order = orderRepo.findOneById(groupBuyOrder.getOrderId());
        if (order == null) {
            return;
        }
        if (WxRefundStatusEnum.SUCCESS.equalsValue(refundStatus)) {
            if (orderRepo.completeRefund(order.getId(), "微信退款查询同步成功",
                    successTime == null ? LocalDateTime.now() : successTime)) {
                this.handleRefundSuccess(groupBuyOrder.getOrderNo());
            }
        } else if (WxRefundStatusEnum.CLOSED.equalsValue(refundStatus)
                || WxRefundStatusEnum.ABNORMAL.equalsValue(refundStatus)) {
            // 微信明确关闭或异常时标记失败，保留记录供后台人工继续处理。
            orderRepo.updateProcessingRefundStatus(order.getId(), RefundStatusEnum.FAILED.getValue(),
                    "微信退款查询同步：" + refundStatus, LocalDateTime.now());
            groupBuyOrderRepo.lambdaUpdate()
                    .eq(GroupBuyOrderEntity::getId, groupBuyOrder.getId())
                    .eq(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.REFUNDING.getValue())
                    .set(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.REFUND_FAILED.getValue())
                    .update();
        }
    }

    /**
     * 解析微信退款成功时间。
     */
    private LocalDateTime parseRefundSuccessTime(String successTime) {
        if (successTime == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(successTime).toLocalDateTime();
        } catch (DateTimeParseException e) {
            log.warn("微信退款成功时间格式错误：{}", successTime);
            return null;
        }
    }

    /**
     * 结算单个团。
     */
    private List<Long> settleGroup(Long groupId) {
        GroupBuyGroupEntity group = groupBuyGroupRepo.findOneByIdForUpdate(groupId);
        if (group == null) {
            return Collections.emptyList();
        }
        if (GroupBuyGroupStatusEnum.SUCCESS.equalsValue(group.getStatus())) {
            // 已成团仅补偿订单状态，不能再次改变团状态。
            this.settleSuccessfulGroupOrders(group.getId());
            return Collections.emptyList();
        }
        if (GroupBuyGroupStatusEnum.FORMING.equalsValue(group.getStatus())
                && group.getMemberCount() != null && group.getMemberCount() >= group.getMinPeople()) {
            // 结算前可能恰好有支付回调完成但尚未更新团状态，先按人数兜底成团。
            boolean completed = groupBuyGroupRepo.lambdaUpdate()
                    .eq(GroupBuyGroupEntity::getId, group.getId())
                    .eq(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.FORMING.getValue())
                    .ge(GroupBuyGroupEntity::getMemberCount, group.getMinPeople())
                    .set(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.SUCCESS.getValue())
                    .set(GroupBuyGroupEntity::getSuccessTime, LocalDateTime.now())
                    .update();
            if (completed) {
                this.settleSuccessfulGroupOrders(group.getId());
            }
            return Collections.emptyList();
        }
        if (GroupBuyGroupStatusEnum.WAIT_PAY.equalsValue(group.getStatus())) {
            // 团长从未支付，团未对外开放，关闭团即可释放最大开团数占位。
            groupBuyGroupRepo.lambdaUpdate()
                    .eq(GroupBuyGroupEntity::getId, group.getId())
                    .eq(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.WAIT_PAY.getValue())
                    .set(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.CLOSED.getValue())
                    .set(GroupBuyGroupEntity::getReservedMemberCount, 0)
                    .update();
        } else if (GroupBuyGroupStatusEnum.FORMING.equalsValue(group.getStatus())) {
            // 活动结束仍未满员的团不可发货，后续对已支付订单自动退款。
            boolean failed = groupBuyGroupRepo.lambdaUpdate()
                    .eq(GroupBuyGroupEntity::getId, group.getId())
                    .eq(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.FORMING.getValue())
                    .set(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.FAILED.getValue())
                    .set(GroupBuyGroupEntity::getFailedTime, LocalDateTime.now())
                    .update();
            if (!failed) {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }

        List<Long> refundIds = new ArrayList<>();
        List<GroupBuyOrderEntity> groupBuyOrders = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getGroupId, group.getId())
                .list();
        for (GroupBuyOrderEntity groupBuyOrder : groupBuyOrders) {
            if (GroupBuyOrderStatusEnum.PAID.equalsValue(groupBuyOrder.getStatus())) {
                Long refundId = this.refundFailedGroupOrder(groupBuyOrder);
                if (refundId != null) {
                    refundIds.add(refundId);
                }
            }
        }
        return refundIds;
    }

    /**
     * 活动结算时关闭未支付订单；微信已支付的竞态订单改走失败退款。
     */
    private void closeUnpaidGroupOrder(GroupBuyOrderEntity groupBuyOrder) {
        OrderEntity order = orderRepo.findOneById(groupBuyOrder.getOrderId());
        if (order == null || !OrderStatusEnum.WAIT_PAY.equalsValue(order.getStatus())) {
            return;
        }
        try {
            boolean paid = wxPayService.closeOrder(order);
            if (paid) {
                // 关闭竞态中微信已完成支付，主动同步本地状态后再按失败团规则退款。
                Transaction transaction = wxPayService.queryOrderByOutTradeNo(order);
                if (transaction == null) {
                    throw new BaseException("微信支付订单状态查询失败");
                }
                transactionTemplate.executeWithoutResult(status -> {
                    if (!orderService.handlePaySuccessByQuery(transaction)) {
                        throw new BaseException("微信支付订单状态同步失败");
                    }
                    this.handleOrderPaid(order.getId());
                });
                return;
            }
            // 微信成功关单后，本地订单、库存和拼团占席需要一并关闭。
            transactionTemplate.executeWithoutResult(status -> {
                OrderEntity current = orderRepo.findOneById(order.getId());
                if (current == null || !OrderStatusEnum.WAIT_PAY.equalsValue(current.getStatus())) {
                    return;
                }
                orderService.cancelOrder(current, "拼团活动结束未支付自动关闭");
                this.handleOrderClosed(current.getId());
            });
        } catch (Exception e) {
            // 保留待支付记录，后续活动结算补偿任务可继续重试关单。
            log.error("活动结算关闭拼团待支付订单失败，orderId={}", order.getId(), e);
        }
    }

    /**
     * 关闭活动结束后仍遗留的待支付参与订单。
     */
    private void closeUnpaidActivityOrders(Long activityId) {
        List<GroupBuyOrderEntity> unpaidOrders = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getActivityId, activityId)
                .eq(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.WAIT_PAY.getValue())
                .list();
        // 覆盖团结算列表未捕获到的待支付订单，作为活动结束的最终关单兜底。
        for (GroupBuyOrderEntity unpaidOrder : unpaidOrders) {
            this.closeUnpaidGroupOrder(unpaidOrder);
        }
    }

    /**
     * 在成员人数达到快照人数时原子完成成团。
     */
    private void tryCompleteGroup(Long groupId) {
        GroupBuyGroupEntity group = groupBuyGroupRepo.getOneById(groupId, "拼团记录不存在");
        if (!GroupBuyGroupStatusEnum.FORMING.equalsValue(group.getStatus())
                || group.getMemberCount() < group.getMinPeople()) {
            return;
        }

        // 使用状态条件更新，使并发支付场景中只有一个请求真正完成成团。
        boolean completed = groupBuyGroupRepo.lambdaUpdate()
                .eq(GroupBuyGroupEntity::getId, group.getId())
                .eq(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.FORMING.getValue())
                .eq(GroupBuyGroupEntity::getMemberCount, group.getMinPeople())
                .set(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.SUCCESS.getValue())
                .set(GroupBuyGroupEntity::getSuccessTime, LocalDateTime.now())
                .update();
        if (!completed) {
            return;
        }
    }

    /**
     * 将已成团的已支付订单解除待成团限制。
     */
    private void settleSuccessfulGroupOrders(Long groupId) {
        List<GroupBuyOrderEntity> paidOrders = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getGroupId, groupId)
                .eq(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.PAID.getValue())
                .list();
        for (GroupBuyOrderEntity paidOrder : paidOrders) {
            // 成团仅解除“待成团”限制，实际发货仍由商家后续操作。
            orderRepo.updateStatus(paidOrder.getOrderId(),
                    OrderStatusEnum.WAIT_GROUP.getValue(), OrderStatusEnum.WAIT_SHIP.getValue());
        }
    }

    /**
     * 为未成团的已支付参与订单创建自动退款。
     */
    private Long refundFailedGroupOrder(GroupBuyOrderEntity groupBuyOrder) {
        boolean refunding = groupBuyOrderRepo.lambdaUpdate()
                .eq(GroupBuyOrderEntity::getId, groupBuyOrder.getId())
                .eq(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.PAID.getValue())
                .set(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.REFUNDING.getValue())
                .update();
        if (!refunding) {
            return null;
        }

        OrderEntity order = orderRepo.getOneById(groupBuyOrder.getOrderId(), "拼团订单不存在");
        // 主订单必须仍是待成团才允许系统退款，防止与发货或人工售后并发冲突。
        boolean orderRefunding = orderRepo.updateStatus(order.getId(),
                OrderStatusEnum.WAIT_GROUP.getValue(), OrderStatusEnum.REFUND.getValue());
        if (!orderRefunding) {
            // 必须回滚前面的拼团参与状态，避免只剩“退款中”却没有退款任务。
            throw new BaseException("拼团订单状态已变更，暂时无法创建退款");
        }
        orderRepo.lambdaUpdate()
                .eq(OrderEntity::getId, order.getId())
                .set(OrderEntity::getRefundStatus, RefundStatusEnum.PROCESSING.getValue())
                .set(OrderEntity::getRefundConfirmTime, LocalDateTime.now())
                .set(OrderEntity::getRefundConfirmRemark, "拼团活动结束未成团，系统自动退款")
                .update();
        // 自动退款后释放占用优惠券，会员可继续使用。
        this.releaseCoupon(order.getId());

        // TODO 切换生产支付后，退款金额应使用订单实际支付金额；测试阶段固定退一分钱。
        BigDecimal testAmount = BigDecimal.valueOf(WxPayService.TEST_PAYMENT_AMOUNT_FEN, 2);
        OrderRefundEntity refund = refundDispatchService.createTask(
                order, testAmount, testAmount, "拼团活动结束未成团，系统自动退款");
        return refund.getId();
    }

    /**
     * 释放自动退款订单占用的会员券。
     */
    private void releaseCoupon(Long orderId) {
        OrderCouponEntity orderCoupon =
                orderCouponRepo.findOneByOrderId(orderId);
        if (orderCoupon != null
                && memberCouponRepo.releaseCoupon(orderCoupon.getMemberCouponId())) {
            // 仅首次成功恢复会员券时回退商家券使用次数，保证重复退款通知幂等。
            mchCouponRepo.decreaseUsed(orderCoupon.getCouponId());
        }
    }
}
