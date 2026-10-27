package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyGroupStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyGroupEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderDeliveryTrackingEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.vo.OrderShopProductVo;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.*;
import com.yeshimin.yeahboot.merchant.domain.vo.OrderDetailVo;
import com.yeshimin.yeahboot.merchant.domain.vo.OrderGroupBuyInfoVo;
import com.yeshimin.yeahboot.service.JuheExpService;
import com.yeshimin.yeahboot.service.MemberPointsService;
import com.yeshimin.yeahboot.service.OrderService;
import com.yeshimin.yeahboot.service.GroupBuyTradeService;
import com.yeshimin.yeahboot.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchOrderService {

    private final PermissionService permissionService;

    private final OrderRepo orderRepo;
    private final ProductSkuRepo productSkuRepo;
    private final OrderItemRepo orderItemRepo;
    private final DeliveryProviderRepo deliveryProviderRepo;
    private final OrderDeliveryTrackingRepo orderDeliveryTrackingRepo;
    private final OrderRefundRepo orderRefundRepo;
    private final GroupBuyOrderRepo groupBuyOrderRepo;
    private final GroupBuyGroupRepo groupBuyGroupRepo;

    private final IdService idService;
    private final JuheExpService juheExpService;
    private final WxPayService wxPayService;
    private final OrderService orderService;
    private final GroupBuyTradeService groupBuyTradeService;
    private final MemberPointsService memberPointsService;

    /**
     * 订单签收超时时间（天）
     */
    @Value("${order-receive-timeout-days:30}")
    private Integer orderReceiveTimeoutDays;

    /**
     * 查询店铺订单
     */
    public IPage<OrderEntity> queryOrder(Long userId, Page<OrderEntity> page, MchOrderQueryDto query) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, query);

        LambdaQueryWrapper<OrderEntity> wrapper = QueryHelper.getQueryWrapper(query);
        if (Boolean.TRUE.equals(query.getRefundPending())) {
            // 与经营首页“退款待处理”指标保持相同口径。
            wrapper.eq(OrderEntity::getStatus, OrderStatusEnum.REFUND.getValue())
                    .in(OrderEntity::getRefundStatus, RefundStatusEnum.APPLYING.getValue(),
                            RefundStatusEnum.PROCESSING.getValue());
        }
        return orderRepo.page(page, wrapper);
    }

    /**
     * 查询订单详情
     */
    public OrderDetailVo queryOrderDetail(Long userId, Long id) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(id);
        // 检查：订单是否属于该商家
        permissionService.checkMch(userId, order);

        // 查询订单明细
        List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());

        // 查询sku
        List<Long> skuIds = orderItems.stream().map(OrderItemEntity::getSkuId).collect(Collectors.toList());
        List<ProductSkuEntity> listSku = productSkuRepo.findListByIds(skuIds);

        // --------------------------------------------------------------------------------

        List<OrderShopProductVo> listOrderShopProductVo =
                orderService.queryOrderShopProductForOrderDetail(listSku, skuIds, orderItems);

        // --------------------------------------------------------------------------------

        // 查询物流跟踪信息
        OrderDeliveryTrackingEntity tracking = orderDeliveryTrackingRepo.findOneByOrderNo(order.getOrderNo());
        JSONObject deliveryTracking = tracking != null && StrUtil.isNotBlank(tracking.getLastSuccessRespData()) ?
                JSONObject.parseObject(tracking.getLastSuccessRespData()) : null;

        OrderDetailVo result = new OrderDetailVo();
        result.setOrder(order);
        result.setShopProducts(listOrderShopProductVo);
        result.setDeliveryTracking(deliveryTracking);
        fillGroupBuyInfo(result, order);
        return result;
    }

    /**
     * 为拼团订单补充团状态，帮助商家在普通订单详情中识别营销订单。
     */
    private void fillGroupBuyInfo(OrderDetailVo result, OrderEntity order) {
        if (!OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
            return;
        }
        GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getOrderId, order.getId())
                .one();
        if (groupBuyOrder == null) {
            return;
        }
        GroupBuyGroupEntity group = groupBuyGroupRepo.findOneById(groupBuyOrder.getGroupId());
        if (group == null) {
            return;
        }
        OrderGroupBuyInfoVo info = new OrderGroupBuyInfoVo();
        info.setGroupNo(group.getGroupNo());
        info.setGroupStatus(group.getStatus());
        info.setMemberCount(group.getMemberCount());
        info.setMinPeople(group.getMinPeople());
        info.setLeader(groupBuyOrder.getIsLeader());
        result.setGroupBuyInfo(info);
    }

    /**
     * 发货
     */
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long userId, OrderShipDto dto) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(dto.getOrderId());
        // 检查：订单是否属于该商家
        permissionService.checkMch(userId, order);

        // 检查：物流公司编码是否存在
        if (deliveryProviderRepo.countByShopIdAndCode(order.getShopId(), dto.getDeliveryProviderCode()) == 0) {
            throw new RuntimeException("物流公司编码不存在");
        }

        // 检查：订单状态是否为待发货
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_SHIP.getValue())) {
            throw new RuntimeException("仅当前订单状态为【待发货】时，才能发货");
        }
        if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
            ensureGroupBuySucceeded(order.getId());
        }
        boolean updateStatusSuccess = orderRepo.updateStatus(
                order.getId(), OrderStatusEnum.WAIT_SHIP.getValue(), OrderStatusEnum.WAIT_RECEIVE.getValue());
        if (!updateStatusSuccess) {
            throw new RuntimeException("订单发货失败，请稍后重试");
        }

        // 更新订单
        LambdaUpdateWrapper<OrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderEntity::getId, order.getId())
                .set(OrderEntity::getDeliveryProviderCode, dto.getDeliveryProviderCode())
                .set(OrderEntity::getTrackingNo, dto.getTrackingNo())
                .set(OrderEntity::getShipTime, LocalDateTime.now())
                // 计算并设置订单签收超时时间
                .set(OrderEntity::getReceiveExpireTime, this.calcOrderReceiveExpireTime());
        orderRepo.update(updateWrapper);

        // 添加订单物流跟踪记录
        boolean r = orderDeliveryTrackingRepo.createOne(order, dto.getTrackingNo(), dto.getDeliveryProviderCode());
        log.info("创建订单物流跟踪记录，结果：{}，订单ID：{}", r, order.getId());
    }

    /**
     * 拼团订单仅在关联团已成团后允许发货。
     */
    private void ensureGroupBuySucceeded(Long orderId) {
        GroupBuyOrderEntity groupBuyOrder = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getOrderId, orderId)
                .one();
        if (groupBuyOrder == null) {
            throw new BaseException("拼团订单关联记录不存在");
        }
        GroupBuyGroupEntity group = groupBuyGroupRepo.getOneById(groupBuyOrder.getGroupId(), "拼团记录不存在");
        if (!GroupBuyGroupStatusEnum.SUCCESS.equalsValue(group.getStatus())) {
            throw new BaseException("拼团尚未成功，暂不能发货");
        }
    }

    /**
     * 更新发货信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateShipInfo(Long userId, UpdateShipInfoDto dto) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(dto.getOrderId());
        // 检查：订单是否属于该商家
        permissionService.checkMchAndShop(userId, order);

        // 检查：物流公司编码是否存在
        if (deliveryProviderRepo.countByShopIdAndCode(order.getShopId(), dto.getDeliveryProviderCode()) == 0) {
            throw new BaseException("物流公司编码不存在");
        }

        // 检查：是否已发货
        if (StrUtil.isBlank(order.getTrackingNo())) {
            throw new BaseException("仅已发货订单，才能更新发货信息");
        }

        LambdaUpdateWrapper<OrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderEntity::getId, order.getId())
                .set(OrderEntity::getDeliveryProviderCode, dto.getDeliveryProviderCode())
                .set(OrderEntity::getTrackingNo, dto.getTrackingNo());
        orderRepo.update(updateWrapper);
    }

    /**
     * 查询订单物流信息
     */
    public JSONObject queryTracking(Long userId, Long orderId) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.findOneById(orderId);
        if (order == null) {
            throw new BaseException("订单不存在");
        }
        // 检查：订单是否属于该商家
        permissionService.checkMch(userId, order);

        // 查询物流信息
        return juheExpService.queryExpress(order.getDeliveryProviderCode(), order.getTrackingNo(), null, null);
    }

    /**
     * 取消超时订单
     * job触发，不需要检查权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelExpiredOrder(Long orderId, String closeReason) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(orderId);
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_PAY.getValue())) {
            return;
        }

        // 先关闭微信订单，防止本地关闭与支付成功并发时错误释放库存、优惠券。
        boolean paid = wxPayService.closeOrder(order.getOrderNo());
        if (paid) {
            syncPaidOrder(order);
            return;
        }

        orderService.cancelOrder(order, closeReason);
        if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
            groupBuyTradeService.handleOrderClosed(order.getId());
        }
    }

    /**
     * 微信关单返回已支付时，查询并同步本地订单状态。
     */
    private void syncPaidOrder(OrderEntity order) {
        Transaction transaction = wxPayService.queryOrderByOutTradeNo(order.getOrderNo());
        if (transaction == null || !Objects.equals(order.getOrderNo(), transaction.getOutTradeNo())
                || !orderService.handlePaySuccessByQuery(transaction)) {
            throw new BaseException("微信支付订单状态同步失败");
        }
        if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
            groupBuyTradeService.handleOrderPaid(order.getId());
        }
        log.info("超时关单发现订单已支付，已同步支付状态，订单ID：{}", order.getId());
    }

    /**
     * 确认收货
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long orderId, String receiveRemark) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(orderId);

        // 检查：订单状态是否为待收货
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_RECEIVE.getValue())) {
            throw new RuntimeException("仅当前订单状态为【待收货】时，才能签收");
        }

        boolean updateStatusSuccess = orderRepo.updateStatus(
                order.getId(), OrderStatusEnum.WAIT_RECEIVE.getValue(), OrderStatusEnum.COMPLETED.getValue());
        if (!updateStatusSuccess) {
            throw new RuntimeException("订单签收失败，请稍后重试");
        }
        order.setStatus(OrderStatusEnum.COMPLETED.getValue());
        order.setReceiveTime(LocalDateTime.now());
        order.setReceiveRemark(receiveRemark);
        order.updateById();
        // 商家代确认收货与会员确认收货共享订单完成奖励，按订单ID确保只发放一次。
        memberPointsService.grantForOrderComplete(order);
    }

    /**
     * 确认退款
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmRefund(Long userId, ConfirmRefundDto dto) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(dto.getOrderId());
        // 检查：订单是否属于该商家
        permissionService.checkMch(userId, order);

        // 检查：仅当订单状态为【退款】，且退款状态为【申请中】或【退款失败】时，才能操作
        if (!(Objects.equals(order.getStatus(), OrderStatusEnum.REFUND.getValue()) &&
                (Objects.equals(order.getRefundStatus(), RefundStatusEnum.APPLYING.getValue())
                        || Objects.equals(order.getRefundStatus(), RefundStatusEnum.FAILED.getValue())))) {
            throw new RuntimeException("仅当订单状态为【退款】，且退款状态为【申请中】或【退款失败】时，才能操作");
        }

        // 更新退款状态
        boolean updateSuccess = orderRepo.updateRefundStatus(order.getId(),
                order.getRefundStatus(), RefundStatusEnum.PROCESSING.getValue());
        if (!updateSuccess) {
            throw new RuntimeException("订单退款失败，请稍后重试");
        }
        order.setRefundStatus(RefundStatusEnum.PROCESSING.getValue());
        order.setRefundConfirmTime(LocalDateTime.now());
        order.setRefundConfirmRemark(dto.getRemark());
        order.updateById();

        // 退款编号
        String refundNo = idService.nextEncodedId();
        // TODO 切换生产支付后，改为订单最终应付金额；测试阶段固定退1分。
        BigDecimal totalAmount = BigDecimal.valueOf(WxPayService.TEST_PAYMENT_AMOUNT_FEN, 2);
        // 首版仅支持全额退款，因此退款金额与测试支付金额一致。
        BigDecimal refundAmount = totalAmount;
        // 退款原因
        String refundReason = dto.getRemark();
        // 添加退款记录
        orderRefundRepo.createOne(order, refundNo, totalAmount, refundAmount, refundReason);

        // 调用第三方退款接口
        long totalAmountFen = WxPayService.TEST_PAYMENT_AMOUNT_FEN;
        long refundAmountFen = WxPayService.TEST_PAYMENT_AMOUNT_FEN;
        Refund refundResp = null;
        try {
            refundResp = wxPayService.refund(
                    order.getOrderNo(), refundNo, refundReason, refundAmountFen, totalAmountFen);
        } catch (Exception e) {
            log.error("调用第三方退款接口失败", e);
        }
        if (refundResp == null) {
            throw new BaseException("调用第三方退款接口失败");
        }
    }

    /**
     * 拒绝退款
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejectRefund(Long userId, RejectRefundDto dto) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(dto.getOrderId());
        // 检查：订单是否属于该商家
        permissionService.checkMch(userId, order);

        // 检查：仅当订单状态为【退款】，且退款状态为【申请中】或【退款失败】时，才能操作
        if (!(Objects.equals(order.getStatus(), OrderStatusEnum.REFUND.getValue()) &&
                (Objects.equals(order.getRefundStatus(), RefundStatusEnum.APPLYING.getValue())
                        || Objects.equals(order.getRefundStatus(), RefundStatusEnum.FAILED.getValue())))) {
            throw new RuntimeException("仅当订单状态为【退款】，且退款状态为【申请中】或【退款失败】时，才能操作");
        }

        // 保持订单处于退款状态，用户可在“退款/售后”中查看拒绝原因并重新发起申请。
        boolean updateSuccess = orderRepo.updateRefundStatus(order.getId(),
                order.getRefundStatus(), RefundStatusEnum.REJECTED.getValue());
        if (!updateSuccess) {
            throw new RuntimeException("订单拒绝退款失败，请稍后重试");
        }
        order.setRefundStatus(RefundStatusEnum.REJECTED.getValue());
        order.setRefundRejectTime(LocalDateTime.now());
        order.setRefundRejectReason(dto.getReason());
        order.updateById();
    }

    // ================================================================================

    /**
     * 计算订单签收超时时间
     */
    private LocalDateTime calcOrderReceiveExpireTime() {
        return LocalDateTime.now().plusDays(orderReceiveTimeoutDays);
    }
}
