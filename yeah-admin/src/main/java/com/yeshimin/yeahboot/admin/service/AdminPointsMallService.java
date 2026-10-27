package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.PointsCategorySaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsOrderCancelDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsOrderFulfillDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsOrderShipDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsProductSaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsSkuSaveDto;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.consts.PointsMallConsts;
import com.yeshimin.yeahboot.data.common.enums.MemberPointsBizTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsFulfillmentTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsOrderOperationEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsOrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsProductTypeEnum;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryProviderEntity;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsCategoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderLogEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.DeliveryProviderOptionVo;
import com.yeshimin.yeahboot.data.repository.DeliveryProviderRepo;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.data.repository.PointsCategoryRepo;
import com.yeshimin.yeahboot.data.repository.PointsOrderLogRepo;
import com.yeshimin.yeahboot.data.repository.PointsOrderRepo;
import com.yeshimin.yeahboot.data.repository.PointsSkuRepo;
import com.yeshimin.yeahboot.data.repository.PointsSpuRepo;
import com.yeshimin.yeahboot.service.MemberPointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 平台积分商城服务
 */
@Service
@RequiredArgsConstructor
public class AdminPointsMallService {

    private final PointsCategoryRepo categoryRepo;
    private final PointsSpuRepo spuRepo;
    private final PointsSkuRepo skuRepo;
    private final PointsOrderRepo orderRepo;
    private final PointsOrderLogRepo logRepo;
    private final MchCouponRepo couponRepo;
    private final DeliveryProviderRepo deliveryProviderRepo;
    private final MemberPointsService pointsService;

    /**
     * 查询积分商品分类
     */
    public List<PointsCategoryEntity> categories() {
        return categoryRepo.lambdaQuery()
                .orderByAsc(PointsCategoryEntity::getSort)
                .orderByAsc(PointsCategoryEntity::getCreateTime)
                .list();
    }

    /**
     * 保存积分商品分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCategory(PointsCategorySaveDto dto) {
        if (dto.getId() != null && Objects.equals(dto.getId(), dto.getParentId())) {
            throw new BaseException("分类不能选择自身作为父分类");
        }
        if (dto.getParentId() != PointsMallConsts.ROOT_CATEGORY_ID
                && categoryRepo.getById(dto.getParentId()) == null) {
            throw new BaseException("父分类不存在");
        }
        long sameNameCount = categoryRepo.lambdaQuery()
                .eq(PointsCategoryEntity::getParentId, dto.getParentId())
                .eq(PointsCategoryEntity::getName, dto.getName())
                .ne(dto.getId() != null, PointsCategoryEntity::getId, dto.getId())
                .count();
        if (sameNameCount > 0) {
            throw new BaseException("同级分类名称不能重复");
        }
        if (dto.getId() != null && !Boolean.TRUE.equals(dto.getIsEnabled())) {
            // 已上架商品依赖可用分类展示，禁止停用仍被上架商品使用的分类。
            long enabledProductCount = spuRepo.lambdaQuery()
                    .eq(PointsSpuEntity::getCategoryId, dto.getId())
                    .eq(PointsSpuEntity::getIsEnabled, true)
                    .count();
            if (enabledProductCount > 0) {
                throw new BaseException("分类下存在已上架商品，不能停用");
            }
        }

        PointsCategoryEntity entity = BeanUtil.copyProperties(dto, PointsCategoryEntity.class);
        if (entity.getId() == null) {
            categoryRepo.save(entity);
        } else if (!categoryRepo.updateById(entity)) {
            throw new BaseException("积分商品分类不存在");
        }
    }

    /**
     * 删除空分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        if (categoryRepo.lambdaQuery().eq(PointsCategoryEntity::getParentId, id).count() > 0) {
            throw new BaseException("请先删除子分类");
        }
        if (spuRepo.lambdaQuery().eq(PointsSpuEntity::getCategoryId, id).count() > 0) {
            throw new BaseException("分类下存在积分商品，不能删除");
        }
        if (!categoryRepo.removeById(id)) {
            throw new BaseException("积分商品分类不存在");
        }
    }

    /**
     * 分页查询积分商品
     */
    public IPage<PointsSpuEntity> products(Page<PointsSpuEntity> page, String name,
                                           Long categoryId, Boolean isEnabled) {
        return spuRepo.lambdaQuery()
                .like(name != null && !name.trim().isEmpty(), PointsSpuEntity::getName, name)
                .eq(categoryId != null, PointsSpuEntity::getCategoryId, categoryId)
                .eq(isEnabled != null, PointsSpuEntity::getIsEnabled, isEnabled)
                .orderByDesc(PointsSpuEntity::getCreateTime)
                .page(page);
    }

    /**
     * 保存积分商品
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(PointsProductSaveDto dto) {
        PointsSpuEntity old = dto.getId() == null ? null : spuRepo.getOneById(dto.getId(), "积分商品不存在");
        validateProduct(dto, old);

        // 校验通过后整体保存商品配置，SKU 仍通过独立入口维护。
        PointsSpuEntity entity = BeanUtil.copyProperties(dto, PointsSpuEntity.class);
        if (entity.getId() == null) {
            spuRepo.save(entity);
        } else {
            spuRepo.updateById(entity);
        }
    }

    /**
     * 删除没有兑换记录的积分商品
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        PointsSpuEntity spu = spuRepo.getOneById(id, "积分商品不存在");
        // 已产生订单的商品必须保留快照关联，只允许下架，不能物理删除。
        if (orderRepo.lambdaQuery().eq(PointsOrderEntity::getSpuId, id).count() > 0) {
            throw new BaseException("商品已有兑换记录，只能下架，不能删除");
        }
        List<PointsSkuEntity> skus = skuRepo.lambdaQuery().eq(PointsSkuEntity::getSpuId, id).list();
        if (!skus.isEmpty()) {
            skuRepo.removeByIds(skus);
        }
        spuRepo.removeById(spu.getId());
    }

    /**
     * 查询商品SKU
     */
    public List<PointsSkuEntity> skus(Long spuId) {
        spuRepo.getOneById(spuId, "积分商品不存在");
        return skuRepo.lambdaQuery()
                .eq(PointsSkuEntity::getSpuId, spuId)
                .orderByAsc(PointsSkuEntity::getSort)
                .list();
    }

    /**
     * 保存商品SKU
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSku(PointsSkuSaveDto dto) {
        PointsSpuEntity spu = spuRepo.getOneById(dto.getSpuId(), "积分商品不存在");
        // SKU 名称在同一积分商品内唯一，避免兑换时无法区分规格。
        long sameNameCount = skuRepo.lambdaQuery()
                .eq(PointsSkuEntity::getSpuId, dto.getSpuId())
                .eq(PointsSkuEntity::getName, dto.getName())
                .ne(dto.getId() != null, PointsSkuEntity::getId, dto.getId())
                .count();
        if (sameNameCount > 0) {
            throw new BaseException("同一商品下SKU名称不能重复");
        }

        if (dto.getId() == null) {
            PointsSkuEntity entity = BeanUtil.copyProperties(dto, PointsSkuEntity.class);
            entity.setTotalStock(dto.getStock());
            skuRepo.save(entity);
            return;
        }

        PointsSkuEntity old = skuRepo.getOneById(dto.getId(), "积分商品SKU不存在");
        if (!Objects.equals(old.getSpuId(), dto.getSpuId())) {
            throw new BaseException("SKU所属商品不可变更");
        }
        if (Boolean.TRUE.equals(spu.getIsEnabled()) && Boolean.TRUE.equals(old.getIsEnabled())
                && !Boolean.TRUE.equals(dto.getIsEnabled())) {
            long enabledCount = skuRepo.lambdaQuery()
                    .eq(PointsSkuEntity::getSpuId, dto.getSpuId())
                    .eq(PointsSkuEntity::getIsEnabled, true)
                    .count();
            if (enabledCount <= 1) {
                throw new BaseException("已上架商品至少保留一个启用SKU");
            }
        }

        // 库存更新按差值调整，totalStock 与可用库存同步保持历史已兑换数量不变。
        int stockDelta = dto.getStock() - old.getStock();
        PointsSkuEntity update = BeanUtil.copyProperties(dto, PointsSkuEntity.class);
        update.setStock(null);
        update.setTotalStock(null);
        skuRepo.updateById(update);
        if (stockDelta != 0 && !skuRepo.adjustStock(old.getId(), stockDelta)) {
            throw new BaseException("SKU库存调整失败");
        }
    }

    /**
     * 删除没有兑换记录的积分商品SKU
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSku(Long id) {
        PointsSkuEntity sku = skuRepo.getOneById(id, "积分商品SKU不存在");
        // 已兑换SKU不能删除，否则订单详情将失去规格关联。
        if (orderRepo.lambdaQuery().eq(PointsOrderEntity::getSkuId, id).count() > 0) {
            throw new BaseException("SKU已有兑换记录，只能停用，不能删除");
        }
        PointsSpuEntity spu = spuRepo.getOneById(sku.getSpuId(), "积分商品不存在");
        if (Boolean.TRUE.equals(spu.getIsEnabled()) && Boolean.TRUE.equals(sku.getIsEnabled())) {
            long enabledCount = skuRepo.lambdaQuery()
                    .eq(PointsSkuEntity::getSpuId, sku.getSpuId())
                    .eq(PointsSkuEntity::getIsEnabled, true)
                    .count();
            if (enabledCount <= 1) {
                throw new BaseException("已上架商品至少保留一个启用SKU");
            }
        }
        skuRepo.removeById(id);
    }

    /**
     * 查询可用于积分兑换的优惠券
     */
    public List<MchCouponEntity> coupons() {
        return couponRepo.lambdaQuery()
                .eq(MchCouponEntity::getIsEnabled, true)
                .eq(MchCouponEntity::getIsPointsExchangeEnabled, true)
                .gt(MchCouponEntity::getEndTime, LocalDateTime.now())
                .orderByDesc(MchCouponEntity::getCreateTime)
                .list();
    }

    /**
     * 查询物流服务商下拉选项
     */
    public List<DeliveryProviderOptionVo> deliveryProviders(String keyword) {
        // 积分订单归平台履约，跨店铺去重后提供统一的物流公司下拉选项。
        return deliveryProviderRepo.queryDistinctOptions(keyword == null ? null : keyword.trim());
    }

    /**
     * 分页查询积分订单
     */
    public IPage<PointsOrderEntity> orders(Page<PointsOrderEntity> page, String orderNo,
                                           Long memberId, String status) {
        if (status != null && !status.trim().isEmpty() && PointsOrderStatusEnum.of(status) == null) {
            throw new BaseException("积分订单状态不正确");
        }
        return orderRepo.lambdaQuery()
                .like(orderNo != null && !orderNo.trim().isEmpty(), PointsOrderEntity::getOrderNo, orderNo)
                .eq(memberId != null, PointsOrderEntity::getMemberId, memberId)
                .eq(status != null && !status.trim().isEmpty(), PointsOrderEntity::getStatus, status)
                .orderByDesc(PointsOrderEntity::getCreateTime)
                .page(page);
    }

    /**
     * 查询积分订单详情
     */
    public PointsOrderEntity orderDetail(Long id) {
        return orderRepo.getOneById(id, "积分订单不存在");
    }

    /**
     * 查询积分订单状态日志
     */
    public List<PointsOrderLogEntity> orderLogs(Long id) {
        orderRepo.getOneById(id, "积分订单不存在");
        return logRepo.lambdaQuery()
                .eq(PointsOrderLogEntity::getOrderId, id)
                .orderByAsc(PointsOrderLogEntity::getCreateTime)
                .list();
    }

    /**
     * 物流订单发货
     */
    @Transactional(rollbackFor = Exception.class)
    public void ship(PointsOrderShipDto dto) {
        PointsOrderEntity order = orderRepo.getOneById(dto.getId(), "积分订单不存在");
        String providerCode = dto.getProviderCode().trim();
        if (!deliveryProviderRepo.lambdaQuery()
                .eq(DeliveryProviderEntity::getCode, providerCode)
                .exists()) {
            throw new BaseException("物流公司不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        // 条件更新确保人工发货与会员取消并发时不会重复进入不同状态。
        if (!orderRepo.shipPending(order.getId(), providerCode, dto.getTrackingNo().trim(), now)) {
            throw new BaseException("当前订单不可发货");
        }
        log(order, PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue(),
                PointsOrderStatusEnum.SHIPPED.getValue(), PointsOrderOperationEnum.PLATFORM_SHIP.getDescription());
    }

    /**
     * 完成人工履约订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void fulfill(PointsOrderFulfillDto dto) {
        PointsOrderEntity order = orderRepo.getOneById(dto.getId(), "积分订单不存在");
        if (!PointsFulfillmentTypeEnum.MANUAL.getValue().equals(order.getFulfillmentType())) {
            throw new BaseException("当前订单不是人工履约订单");
        }
        // 人工权益仅允许从待履约状态完成，已取消或已完成订单不能重复履约。
        if (!orderRepo.completePending(order.getId(), LocalDateTime.now(), dto.getRemark())) {
            throw new BaseException("当前订单不可履约");
        }
        log(order, PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue(),
                PointsOrderStatusEnum.COMPLETED.getValue(), dto.getRemark());
    }

    /**
     * 平台取消待履约订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(PointsOrderCancelDto dto) {
        PointsOrderEntity order = orderRepo.getOneById(dto.getId(), "积分订单不存在");
        if (PointsFulfillmentTypeEnum.AUTO_COUPON.getValue().equals(order.getFulfillmentType())) {
            throw new BaseException("已自动发券的订单不可取消");
        }
        // 先原子取消订单，再执行库存和积分补偿，避免并发重复退回资产。
        if (!orderRepo.cancelPending(order.getId(), LocalDateTime.now(), dto.getReason())) {
            throw new BaseException("当前订单不可取消");
        }
        skuRepo.restoreStock(order.getSkuId(), order.getQuantity());
        pointsService.refundConsume(order.getMemberId(), order.getTotalPoints(), MemberPointsBizTypeEnum.POINTS_CANCEL,
                order.getId(), order.getOrderNo(), "平台取消兑换退还积分");
        log(order, PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue(),
                PointsOrderStatusEnum.CANCELED.getValue(), dto.getReason());
    }

    /**
     * 自动完成指定时间前已发货的积分订单
     */
    @Transactional(rollbackFor = Exception.class)
    public int completeShippedOrdersBefore(LocalDateTime before) {
        // 分批扫描，避免定时任务一次锁定过多已发货订单。
        List<PointsOrderEntity> orders = orderRepo.lambdaQuery()
                .eq(PointsOrderEntity::getStatus, PointsOrderStatusEnum.SHIPPED.getValue())
                .le(PointsOrderEntity::getShipTime, before)
                .last("limit " + PointsMallConsts.AUTO_COMPLETE_BATCH_SIZE)
                .list();
        int completed = 0;
        for (PointsOrderEntity order : orders) {
            if (orderRepo.completeShipped(order.getId(), LocalDateTime.now())) {
                log(order, PointsOrderStatusEnum.SHIPPED.getValue(),
                        PointsOrderStatusEnum.COMPLETED.getValue(),
                        PointsOrderOperationEnum.SYSTEM_AUTO_CONFIRM_RECEIPT.getDescription());
                completed++;
            }
        }
        return completed;
    }

    /**
     * 校验积分商品配置
     */
    private void validateProduct(PointsProductSaveDto dto, PointsSpuEntity old) {
        PointsCategoryEntity category = categoryRepo.getOneById(dto.getCategoryId(), "积分商品分类不存在");
        if (Boolean.TRUE.equals(dto.getIsEnabled()) && !Boolean.TRUE.equals(category.getIsEnabled())) {
            throw new BaseException("停用分类下的商品不能上架");
        }
        if (!dto.getBeginTime().isBefore(dto.getEndTime())) {
            throw new BaseException("兑换结束时间必须晚于开始时间");
        }

        PointsProductTypeEnum productType = PointsProductTypeEnum.of(dto.getProductType());
        PointsFulfillmentTypeEnum fulfillmentType = PointsFulfillmentTypeEnum.of(dto.getFulfillmentType());
        if (productType == null || fulfillmentType == null) {
            throw new BaseException("商品类型或履约方式错误");
        }
        if (productType == PointsProductTypeEnum.PHYSICAL
                && fulfillmentType != PointsFulfillmentTypeEnum.LOGISTICS) {
            throw new BaseException("实物商品必须使用物流发货");
        }
        if (productType == PointsProductTypeEnum.BENEFIT
                && fulfillmentType != PointsFulfillmentTypeEnum.MANUAL) {
            throw new BaseException("虚拟权益必须使用人工履约");
        }
        // 商品类型决定唯一合法履约方式，防止创建配置自相矛盾的积分商品。
        if (productType == PointsProductTypeEnum.COUPON) {
            validateCouponProduct(dto, fulfillmentType);
        } else {
            dto.setCouponId(null);
        }

        // 有历史订单后，改变类型、履约方式或关联优惠券会破坏订单快照语义。
        if (old != null && orderRepo.lambdaQuery().eq(PointsOrderEntity::getSpuId, old.getId()).count() > 0
                && (!Objects.equals(old.getProductType(), dto.getProductType())
                || !Objects.equals(old.getFulfillmentType(), dto.getFulfillmentType())
                || !Objects.equals(old.getCouponId(), dto.getCouponId()))) {
            throw new BaseException("已有兑换订单的商品不可变更商品类型、履约方式或优惠券");
        }
        if (Boolean.TRUE.equals(dto.getIsEnabled())) {
            if (dto.getId() == null) {
                throw new BaseException("请先保存商品并配置SKU，再执行上架");
            }
            long enabledSkuCount = skuRepo.lambdaQuery()
                    .eq(PointsSkuEntity::getSpuId, dto.getId())
                    .eq(PointsSkuEntity::getIsEnabled, true)
                    .count();
            if (enabledSkuCount == 0) {
                throw new BaseException("商品至少需要一个启用的SKU才能上架");
            }
        }
    }

    /**
     * 校验优惠券积分商品配置
     */
    private void validateCouponProduct(PointsProductSaveDto dto,
                                       PointsFulfillmentTypeEnum fulfillmentType) {
        if (fulfillmentType != PointsFulfillmentTypeEnum.AUTO_COUPON || dto.getCouponId() == null) {
            throw new BaseException("优惠券商品必须配置自动发券和优惠券");
        }
        // 自动发券商品的兑换期必须完全落在券的有效期内，避免兑换后立即失效。
        MchCouponEntity coupon = couponRepo.getOneById(dto.getCouponId(), "优惠券不存在");
        if (!Boolean.TRUE.equals(coupon.getIsEnabled())
                || !Boolean.TRUE.equals(coupon.getIsPointsExchangeEnabled())) {
            throw new BaseException("优惠券未开放积分兑换");
        }
        if (dto.getBeginTime().isBefore(coupon.getBeginTime())
                || dto.getEndTime().isAfter(coupon.getEndTime())) {
            throw new BaseException("积分商品兑换时间必须在优惠券有效期内");
        }
    }

    /**
     * 记录积分订单状态变化
     */
    private void log(PointsOrderEntity order, String from, String to, String remark) {
        PointsOrderLogEntity log = new PointsOrderLogEntity();
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        log.setStatusFrom(from);
        log.setStatusTo(to);
        log.setRemark(remark);
        logRepo.save(log);
    }
}
