package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecOptVo;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.MchOrderQueryDto;
import com.yeshimin.yeahboot.merchant.domain.dto.OrderShipDto;
import com.yeshimin.yeahboot.merchant.domain.dto.UpdateShipInfoDto;
import com.yeshimin.yeahboot.merchant.domain.vo.OrderDetailVo;
import com.yeshimin.yeahboot.merchant.domain.vo.OrderShopProductVo;
import com.yeshimin.yeahboot.service.JuheExpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchOrderService {

    private final PermissionService permissionService;

    private final OrderRepo orderRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSpuRepo productSpuRepo;
    private final OrderItemRepo orderItemRepo;
    private final ShopRepo shopRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final DeliveryProviderRepo deliveryProviderRepo;
    private final OrderDeliveryTrackingRepo orderDeliveryTrackingRepo;

    private final JuheExpService juheExpService;

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
                queryOrderShopProductForOrderDetail(listSku, skuIds, orderItems);

        // --------------------------------------------------------------------------------

        // 查询物流跟踪信息
        OrderDeliveryTrackingEntity tracking = orderDeliveryTrackingRepo.findOneByOrderNo(order.getOrderNo());
        JSONObject deliveryTracking = tracking != null && StrUtil.isNotBlank(tracking.getLastSuccessRespData()) ?
                JSONObject.parseObject(tracking.getLastSuccessRespData()) : null;

        OrderDetailVo result = new OrderDetailVo();
        result.setOrder(order);
        result.setShopProducts(listOrderShopProductVo);
        result.setDeliveryTracking(deliveryTracking);
        return result;
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
    public JSONObject queryTracking(Long userId, String orderNo) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.findOneByOrderNo(orderNo);
        if (order == null) {
            throw new BaseException("订单不存在");
        }
        // 检查：订单是否属于该商家
        permissionService.checkMch(userId, order);

        // 查询物流信息
        return juheExpService.queryExpress(order.getDeliveryProviderCode(), order.getTrackingNo(), null, null);
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, String closeReason) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(orderId);

        // 检查：订单状态是否为待付款
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_PAY.getValue())) {
            throw new RuntimeException("仅当前订单状态为【待付款】时，才能取消订单");
        }

        boolean updateStatusSuccess = orderRepo.updateStatus(
                order.getId(), OrderStatusEnum.WAIT_PAY.getValue(), OrderStatusEnum.CLOSED.getValue());
        if (!updateStatusSuccess) {
            throw new RuntimeException("订单取消失败，请稍后重试");
        }
        order.setStatus(OrderStatusEnum.CLOSED.getValue());
        order.setCloseTime(LocalDateTime.now());
        order.setCloseReason(closeReason);
        order.updateById();

        // 查询订单明细
        List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());
        // 释放库存
        for (OrderItemEntity item : orderItems) {
            productSkuRepo.increaseStock(item.getSkuId(), item.getQuantity());
        }

        // 退回优惠券之类的逻辑 TODO 做优惠券的时候再处理
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
    }

    // ================================================================================

    /**
     * 订单详情场景
     */
    private List<OrderShopProductVo> queryOrderShopProductForOrderDetail(List<ProductSkuEntity> listSku,
                                                                         List<Long> skuIds,
                                                                         List<OrderItemEntity> orderItems) {
        // 查询店铺信息
        Set<Long> shopIds = listSku.stream().map(ProductSkuEntity::getShopId).collect(Collectors.toSet());
        List<ShopEntity> listShop = shopRepo.findListByIds(shopIds);
        Map<Long, ShopEntity> mapShop = listShop.stream().collect(Collectors.toMap(ShopEntity::getId, shop -> shop));

        // 查询spu信息
        Set<Long> spuIds = listSku.stream().map(ProductSkuEntity::getSpuId).collect(Collectors.toSet());
        List<ProductSpuEntity> listSpu = productSpuRepo.findListByIds(spuIds);
        Map<Long, ProductSpuEntity> mapSpu = listSpu.stream().collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));

        // 查询sku规格
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
            vo.setShopId(orderItem.getShopId());
            Optional.ofNullable(mapShop.get(orderItem.getShopId())).ifPresent(shop -> vo.setShopName(shop.getShopName()));
            vo.setSpuId(orderItem.getSpuId());
            Optional.ofNullable(mapSpu.get(orderItem.getSpuId())).ifPresent(spu -> {
                vo.setSpuName(spu.getName());
                vo.setSpuMainImage(spu.getMainImage());
            });
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
     * 计算订单签收超时时间
     */
    private LocalDateTime calcOrderReceiveExpireTime() {
        return LocalDateTime.now().plusDays(orderReceiveTimeoutDays);
    }
}
