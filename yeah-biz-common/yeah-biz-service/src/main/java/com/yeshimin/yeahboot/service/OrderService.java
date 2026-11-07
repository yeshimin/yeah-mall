package com.yeshimin.yeahboot.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.TransactionAmount;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.common.utils.WxPayUtils;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.WxRefundStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.domain.vo.OrderShopProductVo;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecOptVo;
import com.yeshimin.yeahboot.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单详情，多端共用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductSpuRepo productSpuRepo;
    private final ShopRepo shopRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final OrderRepo orderRepo;
    private final OrderRefundRepo orderRefundRepo;
    private final OrderCouponRepo orderCouponRepo;
    private final ProductSkuRepo productSkuRepo;
    private final OrderItemRepo orderItemRepo;
    private final SeckillSkuRepo seckillSkuRepo;
    private final GroupBuySkuRepo groupBuySkuRepo;
    private final GroupBuyOrderRepo groupBuyOrderRepo;
    private final MemberCouponRepo memberCouponRepo;
    private final MchCouponRepo mchCouponRepo;

    private final CacheService cacheService;
    private final WxPayService wxPayService;
    private final RefundDispatchService refundDispatchService;
    private final MemberPointsService memberPointsService;
    private final ShopSettlementService shopSettlementService;
    private final TransactionTemplate transactionTemplate;

    /**
     * 订单详情场景
     */
    public List<OrderShopProductVo> queryOrderShopProductForOrderDetail(
            OrderEntity order, List<OrderItemEntity> orderItems) {
        // 查询店铺信息
        Set<Long> shopIds = orderItems.stream().map(OrderItemEntity::getShopId).collect(Collectors.toSet());
        List<ShopEntity> listShop = shopRepo.findListByIds(shopIds);
        Map<Long, ShopEntity> mapShop = listShop.stream().collect(Collectors.toMap(ShopEntity::getId, shop -> shop));

        // 秒杀订单保存的是秒杀SKU ID，不能拿它查询普通商品规格表。
        List<Long> skuIds = OrderTypeEnum.SECKILL.equalsValue(order.getOrderType())
                ? Collections.emptyList()
                : orderItems.stream().map(OrderItemEntity::getSkuId).collect(Collectors.toList());
        List<ProductSkuSpecEntity> listSkuSpec = productSkuSpecRepo.findListBySkuIds(skuIds);
        Map<Long, List<ProductSkuSpecEntity>> mapSkuSpecs = listSkuSpec.stream().collect(Collectors.groupingBy(ProductSkuSpecEntity::getSkuId));
        // 查询specs
        Set<Long> specIds = listSkuSpec.stream().map(ProductSkuSpecEntity::getSpecId).collect(Collectors.toSet());
        Map<Long, ProductSpecDefEntity> mapSpecDef = productSpecDefRepo.findListByIds(specIds)
                .stream().collect(Collectors.toMap(ProductSpecDefEntity::getId, v -> v));
        // 查询opts
        Set<Long> optIds = listSkuSpec.stream().map(ProductSkuSpecEntity::getOptId).collect(Collectors.toSet());
        Map<Long, ProductSpecOptDefEntity> mapOptDef = productSpecOptDefRepo.findListByIds(optIds)
                .stream().collect(Collectors.toMap(ProductSpecOptDefEntity::getId, v -> v));

        return orderItems.stream().map(orderItem -> {
            OrderShopProductVo vo = new OrderShopProductVo();
            vo.setId(orderItem.getId());
            vo.setShopId(orderItem.getShopId());
            Optional.ofNullable(mapShop.get(orderItem.getShopId())).ifPresent(shop -> vo.setShopName(shop.getShopName()));
            vo.setSpuId(orderItem.getSpuId());
            // 订单详情使用下单快照，避免商品改名、删除或营销SKU撞ID后展示错误。
            vo.setSpuName(orderItem.getSpuName());
            vo.setSpuMainImage(orderItem.getSpuMainImage());
            vo.setSkuId(orderItem.getSkuId());
            vo.setSkuName(orderItem.getSkuName());
            vo.setPrice(orderItem.getUnitPrice());
            vo.setQuantity(orderItem.getQuantity());

            // specs
            List<ProductSkuSpecEntity> skuSpecs =
                    mapSkuSpecs.getOrDefault(orderItem.getSkuId(), Collections.emptyList());
            List<ProductSpecOptVo> listSpecOptVo = skuSpecs.stream().map(skuSpec -> {
                ProductSpecOptVo optVo = new ProductSpecOptVo();
                // set spec
                Optional.ofNullable(mapSpecDef.get(skuSpec.getSpecId())).ifPresent(specDef -> {
                    optVo.setSpecId(specDef.getId());
                    optVo.setSpecName(specDef.getSpecName());
                });
                // set opt
                Optional.ofNullable(mapOptDef.get(skuSpec.getOptId())).ifPresent(optDef -> {
                    optVo.setOptId(optDef.getId());
                    optVo.setOptName(optDef.getOptName());
                });
                // set sort
                optVo.setSort(skuSpec.getSort());
                return optVo;
            }).collect(Collectors.toList());
            vo.setSpecs(listSpecOptVo);

            // 订单ID
            vo.setOrderId(orderItem.getOrderId());

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(OrderEntity order, String closeReason) {
        // 检查：订单状态是否为待付款
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_PAY.getValue())) {
            throw new RuntimeException("仅当前订单状态为【待付款】时，才能取消订单");
        }

        boolean updateStatusSuccess = orderRepo.updateStatus2(
                order.getId(), OrderStatusEnum.WAIT_PAY.getValue(), OrderStatusEnum.CLOSED.getValue(),
                LocalDateTime.now(), closeReason);
        if (!updateStatusSuccess) {
            throw new RuntimeException("订单取消失败，请稍后重试");
        }

        // 状态成功切为关闭后才释放库存，避免重复取消导致多次回补。
        List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());
        for (OrderItemEntity item : orderItems) {
            if (OrderTypeEnum.SECKILL.equalsValue(order.getOrderType())) {
                // 秒杀订单明细保存秒杀SKU ID，直接回补秒杀库存。
                seckillSkuRepo.increaseStock(item.getSkuId(), item.getQuantity());
            } else if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
                GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                        .eq(GroupBuyOrderEntity::getOrderId, order.getId()).one();
                if (groupBuyOrder != null) {
                    GroupBuySkuEntity groupBuySku = groupBuySkuRepo.findOneById(groupBuyOrder.getGroupBuySkuId());
                    if (groupBuySku != null && Boolean.TRUE.equals(groupBuySku.getIsStockReserved())) {
                        // 活动未结算时库存仍预留在拼团SKU中。
                        groupBuySkuRepo.increaseStock(groupBuySku.getId(), item.getQuantity());
                    } else if (groupBuySku != null) {
                        // 活动结束后拼团余量已释放，取消订单需直接回补源SKU。
                        productSkuRepo.increaseStock(groupBuySku.getSkuId(), item.getQuantity());
                    }
                }
            } else {
                productSkuRepo.increaseStock(item.getSkuId(), item.getQuantity());
            }
        }

        // 订单取消后恢复已占用的会员券。
        this.releaseCouponIfNecessary(order.getId());
        // 秒杀订单取消后同步释放Redis名额、事件和库存，允许用户重新参与。
        this.tryReleaseSeckillCache(order);
    }

    /**
     * 处理支付成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaySuccess(WxPayUtils.Notification notification) {
        WxPayUtils.OrderInfo orderInfo = notification.getOrderInfo();
        if (orderInfo == null || orderInfo.getAmount() == null || orderInfo.getSuccessTime() == null) {
            log.error("处理支付成功失败，支付回调缺少订单金额或支付时间");
            return false;
        }
        return this.handlePaySuccess(orderInfo.getOutTradeNo(), orderInfo.getTransactionId(),
                orderInfo.getAmount().getTotal(), orderInfo.getAmount().getCurrency(),
                orderInfo.getSuccessTime().toLocalDateTime());
    }

    /**
     * 根据微信订单查询结果同步支付成功状态。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaySuccessByQuery(Transaction transaction) {
        if (transaction == null || transaction.getTradeState() != Transaction.TradeStateEnum.SUCCESS) {
            log.error("同步微信支付结果失败，交易状态不是支付成功");
            return false;
        }
        TransactionAmount amount = transaction.getAmount();
        if (amount == null || transaction.getSuccessTime() == null) {
            log.error("同步微信支付结果失败，缺少订单金额或支付时间");
            return false;
        }
        try {
            // 主动查询与回调共用同一金额校验和状态更新逻辑，确保结果一致。
            return this.handlePaySuccess(transaction.getOutTradeNo(), transaction.getTransactionId(),
                    amount.getTotal(), amount.getCurrency(),
                    OffsetDateTime.parse(transaction.getSuccessTime()).toLocalDateTime());
        } catch (DateTimeParseException e) {
            log.error("同步微信支付结果失败，支付时间格式错误", e);
            return false;
        }
    }

    /**
     * 校验支付金额并更新本地订单状态。
     */
    private boolean handlePaySuccess(String outTradeNo, String transactionId, Integer amountTotal, String currency,
                                     LocalDateTime paySuccessTime) {
        // 查询业务订单
        OrderEntity order = orderRepo.findOneByOrderNo(outTradeNo);
        // 如果业务数据不存在，返回成功，避免重复通知
        if (order == null) {
            log.warn("处理支付成功失败，订单不存在，outTradeNo={}", outTradeNo);
            return true;
        }
        if (!this.isExpectedPaymentAmount(order, amountTotal, currency)) {
            log.error("处理支付成功失败，回调支付金额与订单实际扣款金额不一致，outTradeNo={}", outTradeNo);
            return false;
        }

        // 仅处理当前状态为【待付款】的情况，其他情况不做处理，返回成功
        if (!OrderStatusEnum.WAIT_PAY.getValue().equals(order.getStatus())) {
            log.info("处理支付成功失败，订单状态非待付款，outTradeNo={}, status={}", outTradeNo, order.getStatus());
            return true;
        }

        // 更新订单状态
        // 拼团订单已付款后必须先等待成团，成团成功后再允许商家发货。
        String targetStatus = OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())
                ? OrderStatusEnum.WAIT_GROUP.getValue() : OrderStatusEnum.WAIT_SHIP.getValue();
        boolean success = orderRepo.updateStatus(order.getId(),
                OrderStatusEnum.WAIT_PAY.getValue(), targetStatus);
        // 可能已经处理过了，返回成功
        if (!success) {
            log.info("处理支付成功失败，订单状态更新失败，可能已经处理过了，outTradeNo={}", outTradeNo);
            return true;
        }

        // 更新支付成功时间
        LambdaUpdateWrapper<OrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderEntity::getId, order.getId())
                .set(OrderEntity::getPaySuccessTime, paySuccessTime)
                .set(OrderEntity::getWxTransactionId, transactionId);
        orderRepo.update(updateWrapper);

        // 订单状态仅能从待支付成功迁移一次，因此这里累计销量不会被重复回调重复执行。
        this.increaseProductSales(order);

        return true;
    }

    /**
     * 按订单类型累计源商品销量
     */
    private void increaseProductSales(OrderEntity order) {
        List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());
        if (orderItems.isEmpty()) {
            log.warn("支付成功订单没有订单明细，跳过销量累计，orderId={}", order.getId());
            return;
        }

        Map<Long, Integer> salesBySpuId = new HashMap<>();
        if (OrderTypeEnum.SECKILL.equalsValue(order.getOrderType())) {
            Set<Long> seckillSkuIds = orderItems.stream().map(OrderItemEntity::getSkuId).collect(Collectors.toSet());
            Map<Long, SeckillSkuEntity> seckillSkuMap = seckillSkuRepo.findListByIds(seckillSkuIds).stream()
                    .collect(Collectors.toMap(SeckillSkuEntity::getId, sku -> sku));
            for (OrderItemEntity item : orderItems) {
                SeckillSkuEntity seckillSku = seckillSkuMap.get(item.getSkuId());
                if (seckillSku == null || seckillSku.getSpuId() == null) {
                    log.warn("秒杀订单明细无法映射源商品，跳过销量累计，orderItemId={}", item.getId());
                    continue;
                }
                salesBySpuId.merge(seckillSku.getSpuId(), item.getQuantity(), Integer::sum);
            }
        } else {
            // 普通订单与拼团订单明细均保存源商品SPU ID。
            for (OrderItemEntity item : orderItems) {
                if (item.getSpuId() != null) {
                    salesBySpuId.merge(item.getSpuId(), item.getQuantity(), Integer::sum);
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : salesBySpuId.entrySet()) {
            if (!productSpuRepo.increaseSales(entry.getKey(), entry.getValue())) {
                log.warn("源商品不存在，销量累计未生效，spuId={}, quantity={}", entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 处理订单关闭
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handleOrderClose(WxPayUtils.Notification notification) {
        String outTradeNo = notification.getOrderInfo().getOutTradeNo();

        // 查询业务订单
        OrderEntity order = orderRepo.findOneByOrderNo(outTradeNo);
        // 如果业务数据不存在，返回成功，避免重复通知
        if (order == null) {
            log.warn("处理订单关闭失败，订单不存在，outTradeNo={}", outTradeNo);
            return true;
        }

        // 仅处理当前状态为【待付款】的情况，其他情况不做处理，返回成功
        if (!OrderStatusEnum.WAIT_PAY.getValue().equals(order.getStatus())) {
            log.info("处理订单关闭失败，订单状态非待付款，outTradeNo={}, status={}", outTradeNo, order.getStatus());
            return true;
        }

        // 更新订单状态
        boolean success = orderRepo.updateStatus2(
                order.getId(), OrderStatusEnum.WAIT_PAY.getValue(), OrderStatusEnum.CLOSED.getValue(),
                LocalDateTime.now(), "当前场景：秒杀订单超时未支付，定时任务触发关闭订单，第三方回调");
        if (!success) {
            log.info("处理订单关闭失败，订单状态更新失败，可能已经处理过了，outTradeNo={}", outTradeNo);
            return true;
        }

        // 查询订单明细
        List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());
        // 释放库存
        for (OrderItemEntity item : orderItems) {
            if (OrderTypeEnum.SECKILL.equalsValue(order.getOrderType())) {
                seckillSkuRepo.increaseStock(item.getSkuId(), item.getQuantity());
            } else if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
                GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                        .eq(GroupBuyOrderEntity::getOrderId, order.getId()).one();
                if (groupBuyOrder != null) {
                    GroupBuySkuEntity groupBuySku = groupBuySkuRepo.findOneById(groupBuyOrder.getGroupBuySkuId());
                    if (groupBuySku != null && Boolean.TRUE.equals(groupBuySku.getIsStockReserved())) {
                        groupBuySkuRepo.increaseStock(groupBuySku.getId(), item.getQuantity());
                    } else if (groupBuySku != null) {
                        productSkuRepo.increaseStock(groupBuySku.getSkuId(), item.getQuantity());
                    }
                }
            } else {
                productSkuRepo.increaseStock(item.getSkuId(), item.getQuantity());
            }
        }

        this.releaseCouponIfNecessary(order.getId());

        // 如果是秒杀订单，需要清除相应缓存数据
        this.tryReleaseSeckillCache(order);
        return true;
    }

    /**
     * 如果是秒杀订单，需要清除相应缓存数据
     */
    private void tryReleaseSeckillCache(OrderEntity order) {
        // 如果是秒杀订单，需要清除相应缓存数据
        if (Objects.equals(order.getOrderType(), OrderTypeEnum.SECKILL.getIntValue())) {
            // 查询订单明细，秒杀订单只有一个orderItem
            List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());
            Long skuId = orderItems.get(0).getSkuId();
            Long userId = order.getMemberId();

            // 删除抢占名额、旧版下单记录和event信息；新版attempt下单记录随活动缓存统一过期。
            String seckillEventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, userId);
            String quotaKey = String.format(BizConsts.SECKILL_QUOTA_KEY, skuId);
            String orderKey = String.format(BizConsts.SECKILL_ORDER_KEY, skuId);
            cacheService.delete(seckillEventKey);
            cacheService.removeMember(quotaKey, String.valueOf(userId));
            cacheService.removeMember(orderKey, String.valueOf(userId));
            SeckillSkuEntity seckillSku = seckillSkuRepo.findOneById(skuId);
            if (seckillSku != null) {
                cacheService.removeMember(String.format(
                        BizConsts.SECKILL_SPU_QUOTA_KEY, seckillSku.getSeckillSpuId()), String.valueOf(userId));
            }
            // 释放库存
            cacheService.increase(String.format(BizConsts.SECKILL_STOCK_KEY, skuId));
        }
    }

    /**
     * 处理退款成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRefundNotify(WxPayUtils.Notification notification) {
        WxPayUtils.RefundInfo refundInfo = notification.getRefundInfo();
        if (refundInfo == null || !this.isExpectedTestRefundAmount(refundInfo.getAmount())) {
            log.error("处理退款通知失败，回调退款金额不符合测试金额策略");
            return false;
        }
        String orderNo = refundInfo.getOutTradeNo();
        String refundNo = refundInfo.getOutRefundNo();

        // 查询订单记录
        OrderEntity order = orderRepo.findOneByOrderNo(orderNo);
        if (order == null) {
            log.warn("处理退款通知失败，订单不存在，orderNo={}", orderNo);
            return true;
        }
        // 查询退款记录
        OrderRefundEntity orderRefund = orderRefundRepo.findOneByRefundNo(refundNo);
        // 如果业务数据不存在，返回成功，避免重复通知
        if (orderRefund == null) {
            log.warn("处理退款通知失败，退款记录不存在，refundNo={}", refundNo);
            return true;
        }

        // 退款通知可能先到处理中状态，先原样同步退款单再按状态更新主订单。
        LocalDateTime refundSuccessTime = refundInfo.getSuccessTime() == null
                ? null : refundInfo.getSuccessTime().toLocalDateTime();
        boolean refundResultUpdated = orderRefundRepo.updateWxResult(
                orderRefund.getId(), null, refundInfo.getRefundStatus(), refundSuccessTime,
                refundInfo.getUserReceivedAccount(), notification.getSummary(),
                refundInfo.getAmount().getTotal(), refundInfo.getAmount().getRefund());

        // 如果订单状态为【退款】，更新订单退款状态
        if (refundResultUpdated && Objects.equals(order.getStatus(), OrderStatusEnum.REFUND.getValue())) {
            WxRefundStatusEnum wxRefundStatus = WxRefundStatusEnum.of(refundInfo.getRefundStatus());
            switch (Objects.requireNonNull(wxRefundStatus)) {
                case SUCCESS:
                    // 退款成功时间允许为空，异常情况下用当前时间保证状态可落库。
                    if (orderRepo.completeRefund(order.getId(), notification.getSummary(),
                            refundSuccessTime == null ? LocalDateTime.now() : refundSuccessTime)) {
                        // 订单退款状态落库成功后再统一回补库存、优惠券等资源，避免回调重试重复补偿。
                        this.restoreRefundResources(order);
                        // 已发放的订单完成积分需要随退款回退，记账服务内部按订单幂等处理。
                        memberPointsService.reverseForOrderRefund(order);
                        shopSettlementService.recordRefundCompleted(order, orderRefund);
                    }
                    break;
                case CLOSED:
                case ABNORMAL:
                    orderRepo.updateProcessingRefundStatus(order.getId(), RefundStatusEnum.FAILED.getValue(),
                            notification.getSummary(), LocalDateTime.now());
                    break;
                case PROCESSING:
                    orderRepo.updateProcessingRefundStatus(order.getId(), RefundStatusEnum.PROCESSING.getValue(),
                            notification.getSummary(), LocalDateTime.now());
                    break;
                default:
                    break;
            }
        }

        if (refundResultUpdated) {
            // 只有退款结果确实被当前回调推进时才调整任务，旧PROCESSING回调不能恢复已结束任务。
            refundDispatchService.markFromWxCallback(orderRefund.getId(), refundInfo.getRefundStatus());
        }

        return true;
    }

    /**
     * 主动查询普通和秒杀订单的退款状态，避免微信退款回调丢失后长期停留在处理中。
     */
    public void reconcileRefund(Long refundId) {
        OrderRefundEntity orderRefund = orderRefundRepo.findOneById(refundId);
        if (orderRefund == null) {
            return;
        }
        OrderEntity order = orderRepo.findOneById(orderRefund.getOrderId());
        if (order == null) {
            String processingToken = refundDispatchService.claimDue(orderRefund.getId());
            if (processingToken != null) {
                refundDispatchService.markFailed(orderRefund.getId(), processingToken, "OrderNotFound");
            }
            return;
        }
        if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
            return;
        }
        String processingToken = refundDispatchService.claimDue(orderRefund.getId());
        if (processingToken == null) {
            return;
        }
        Refund result;
        try {
            result = wxPayService.queryRefundByOutRefundNo(order, orderRefund.getRefundNo());
        } catch (Exception e) {
            log.warn("主动查询退款状态失败，orderNo={}, refundNo={}, error={}",
                    order.getOrderNo(), orderRefund.getRefundNo(), e.getClass().getSimpleName());
            refundDispatchService.retryClaimed(order, orderRefund, processingToken);
            return;
        }
        try {
            transactionTemplate.executeWithoutResult(status ->
                    this.syncRefundQueryResult(order, orderRefund, result, processingToken));
            refundDispatchService.markFromWxQuery(orderRefund.getId(), processingToken,
                    result == null || result.getStatus() == null ? null : result.getStatus().name());
        } catch (Exception e) {
            log.error("同步退款查询结果失败，orderNo={}, refundNo={}, error={}",
                    order.getOrderNo(), orderRefund.getRefundNo(), e.getClass().getSimpleName());
            refundDispatchService.markRetry(orderRefund.getId(), processingToken, e);
        }
    }

    /**
     * 同步微信退款查询结果
     */
    private void syncRefundQueryResult(OrderEntity order, OrderRefundEntity orderRefund, Refund refund,
                                       String processingToken) {
        if (refund == null || refund.getStatus() == null) {
            return;
        }
        String refundStatus = refund.getStatus().name();
        LocalDateTime successTime = this.parseRefundSuccessTime(refund.getSuccessTime());
        // 先保存微信侧退款明细，便于失败或处理中状态继续追踪。
        boolean refundResultUpdated = orderRefundRepo.updateWxResult(
                orderRefund.getId(), processingToken, refundStatus, successTime,
                refund.getUserReceivedAccount(), null,
                refund.getAmount() == null ? null : refund.getAmount().getTotal(),
                refund.getAmount() == null ? null : refund.getAmount().getRefund());
        if (!refundResultUpdated) {
            return;
        }

        // 仅在订单状态首次成功推进时回补资源并记录结算冲销，保证重复查询幂等。
        if (WxRefundStatusEnum.SUCCESS.equalsValue(refundStatus)) {
            if (orderRepo.completeRefund(order.getId(), "微信退款查询同步成功",
                    successTime == null ? LocalDateTime.now() : successTime)) {
                this.restoreRefundResources(order);
                memberPointsService.reverseForOrderRefund(order);
                shopSettlementService.recordRefundCompleted(order, orderRefund);
            }
        } else if (WxRefundStatusEnum.CLOSED.equalsValue(refundStatus)
                || WxRefundStatusEnum.ABNORMAL.equalsValue(refundStatus)) {
            orderRepo.updateProcessingRefundStatus(order.getId(), RefundStatusEnum.FAILED.getValue(),
                    "微信退款查询同步：" + refundStatus, LocalDateTime.now());
        }
    }

    /**
     * 退款成功后一次性回补未发货订单占用的库存和优惠券。
     */
    private void restoreRefundResources(OrderEntity order) {
        // 已发货或已签收的仅退款不回补库存，避免用户保留商品时产生虚增库存。
        if (order.getShipTime() == null) {
            List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());
            for (OrderItemEntity item : orderItems) {
                if (OrderTypeEnum.SECKILL.equalsValue(order.getOrderType())) {
                    seckillSkuRepo.increaseStock(item.getSkuId(), item.getQuantity());
                } else if (!OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
                    productSkuRepo.increaseStock(item.getSkuId(), item.getQuantity());
                }
            }
        }
        // 优惠券仅恢复使用状态，保留原有效期；已过期优惠券仍按原时间自然过期。
        this.releaseCouponIfNecessary(order.getId());
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
     * 退款成功后释放订单使用的优惠券。
     */
    private void releaseCouponIfNecessary(Long orderId) {
        OrderCouponEntity orderCoupon = orderCouponRepo.findOneByOrderId(orderId);
        if (orderCoupon == null) {
            return;
        }

        // 会员券仓储通过未使用条件更新恢复状态，重复触发不会造成券数错误。
        if (memberCouponRepo.releaseCoupon(orderCoupon.getMemberCouponId())) {
            mchCouponRepo.decreaseUsed(orderCoupon.getCouponId());
        }
    }

    /**
     * TODO 切换生产支付后，使用订单最终应付金额校验微信回调金额。
     */
    private boolean isExpectedPaymentAmount(OrderEntity order, Integer amountTotal, String currency) {
        BigDecimal payAmount = order.getPayAmount() == null
                ? BigDecimal.valueOf(WxPayService.TEST_PAYMENT_AMOUNT_FEN, 2) : order.getPayAmount();
        int expectedFen = payAmount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).intValue();
        return amountTotal != null && amountTotal == expectedFen && "CNY".equals(currency);
    }

    /**
     * TODO 切换生产支付后，使用订单退款记录中的实际金额校验微信退款回调。
     */
    private boolean isExpectedTestRefundAmount(WxPayUtils.RefundAmount amount) {
        return amount != null
                && amount.getTotal() == WxPayService.TEST_PAYMENT_AMOUNT_FEN
                && amount.getRefund() == WxPayService.TEST_PAYMENT_AMOUNT_FEN;
    }
}
