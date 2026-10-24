package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchDashboardVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchStockWarningVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家经营首页服务
 */
@Service
@RequiredArgsConstructor
public class MchDashboardService {

    private static final int STOCK_WARNING_THRESHOLD = 10;
    private static final int STOCK_WARNING_LIMIT = 10;

    private final PermissionService permissionService;
    private final OrderRepo orderRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSpuRepo productSpuRepo;

    /**
     * 查询当前店铺经营概览。
     */
    public MchDashboardVo queryOverview(Long mchId, Long shopId) {
        permissionService.checkShop(mchId, shopId);

        LocalDateTime todayBegin = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowBegin = todayBegin.plusDays(1);
        MchDashboardVo result = new MchDashboardVo();
        result.setTodayBeginTime(todayBegin);
        result.setTodayEndTime(tomorrowBegin.minusSeconds(1));
        result.setWaitShipCount(orderRepo.lambdaQuery()
                .eq(OrderEntity::getShopId, shopId)
                .eq(OrderEntity::getStatus, OrderStatusEnum.WAIT_SHIP.getValue())
                .count());
        result.setRefundPendingCount(orderRepo.lambdaQuery()
                .eq(OrderEntity::getShopId, shopId)
                .eq(OrderEntity::getStatus, OrderStatusEnum.REFUND.getValue())
                .in(OrderEntity::getRefundStatus, RefundStatusEnum.APPLYING.getValue(),
                        RefundStatusEnum.PROCESSING.getValue())
                .count());

        List<ProductSkuEntity> lowStockSkus = productSkuRepo.page(new Page<ProductSkuEntity>(1, STOCK_WARNING_LIMIT),
                new LambdaQueryWrapper<ProductSkuEntity>()
                        .eq(ProductSkuEntity::getShopId, shopId)
                        .le(ProductSkuEntity::getStock, STOCK_WARNING_THRESHOLD)
                        .orderByAsc(ProductSkuEntity::getStock))
                .getRecords();
        result.setStockWarningCount(productSkuRepo.lambdaQuery()
                .eq(ProductSkuEntity::getShopId, shopId)
                .le(ProductSkuEntity::getStock, STOCK_WARNING_THRESHOLD)
                .count());
        result.setStockWarningThreshold(STOCK_WARNING_THRESHOLD);
        result.setStockWarnings(toStockWarnings(lowStockSkus));

        List<OrderEntity> paidOrders = orderRepo.lambdaQuery()
                .eq(OrderEntity::getShopId, shopId)
                .ge(OrderEntity::getPaySuccessTime, todayBegin)
                .lt(OrderEntity::getPaySuccessTime, tomorrowBegin)
                .notIn(OrderEntity::getStatus, OrderStatusEnum.CLOSED.getValue(),
                        OrderStatusEnum.REFUND.getValue(), OrderStatusEnum.AFTER_SALE.getValue())
                .list();
        result.setTodayOrderCount((long) paidOrders.size());
        result.setTodaySalesAmount(paidOrders.stream()
                .map(OrderEntity::getFinalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return result;
    }

    private List<MchStockWarningVo> toStockWarnings(List<ProductSkuEntity> skus) {
        if (skus.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, ProductSpuEntity> spuMap = productSpuRepo.findListByIds(skus.stream()
                        .map(ProductSkuEntity::getSpuId)
                        .collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));
        return skus.stream().map(sku -> {
            MchStockWarningVo vo = new MchStockWarningVo();
            vo.setSkuId(sku.getId());
            vo.setSpuId(sku.getSpuId());
            vo.setSkuName(sku.getName());
            vo.setStock(sku.getStock());
            ProductSpuEntity spu = spuMap.get(sku.getSpuId());
            vo.setSpuName(spu == null ? "商品已删除" : spu.getName());
            return vo;
        }).collect(Collectors.toList());
    }
}
