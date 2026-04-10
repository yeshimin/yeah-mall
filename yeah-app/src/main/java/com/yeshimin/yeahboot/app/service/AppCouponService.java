package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.AvailableCouponQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.OrderPreviewItemDto;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.CouponTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.CouponUseRangeEnum;
import com.yeshimin.yeahboot.data.common.enums.MemberCouponQueryConditionEnum;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.CouponQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import com.yeshimin.yeahboot.data.domain.vo.MemberCouponVo;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCouponService {

    private final MchCouponRepo mchCouponRepo;
    private final MemberCouponRepo memberCouponRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSpuRepo productSpuRepo;

    /**
     * 查询领券中心优惠券列表
     */
    public Page<CouponVo> queryCenterList(Long userId, Page<CouponVo> page, CouponCenterQueryDto query) {
        return mchCouponRepo.queryCenterList(userId, page, query);
    }

    /**
     * 查询用户领取的优惠券列表
     */
    public Page<MemberCouponVo> queryReceiveList(Long userId, Page<MemberCouponVo> page, CouponQueryDto query) {
        return memberCouponRepo.queryReceiveList(userId, page, query);
    }

    /**
     * 用户领取优惠券
     */
    @Transactional(rollbackFor = Exception.class)
    public void receive(Long userId, Long couponId) {
        // 检查：优惠券是否存在
        MchCouponEntity coupon = mchCouponRepo.getOneById(couponId);
        // 检查：优惠券是否可用
        if (!coupon.getIsEnabled()) {
            throw new BaseException("优惠券不可用");
        }
        // 检查：优惠券是否已被领取完
        if (coupon.getReceived() >= coupon.getQuantity()) {
            throw new BaseException("优惠券已领取完");
        }
        // 查询用于对该优惠券的领取数量
        long receivedCount = memberCouponRepo.countByMemberIdAndCouponId(userId, couponId);
        // 检查：是否已达到领取上限
        if (receivedCount >= coupon.getPerLimit()) {
            throw new BaseException("已超过领取数量限制");
        }

        // 领取优惠券
        int updateCount = mchCouponRepo.receiveCoupon(couponId, 1);
        if (updateCount == 0) {
            throw new BaseException("优惠券已领取完");
        }

        MemberCouponEntity memberCoupon =
                BeanUtil.copyProperties(coupon, MemberCouponEntity.class, CommonConsts.COPY_IGNORE_FIELDS);
        memberCoupon.setMemberId(userId);
        memberCoupon.setCouponId(coupon.getId());
        memberCouponRepo.save(memberCoupon);
    }

    /**
     * 查询可使用的优惠券列表
     */
    public List<MemberCouponVo> queryAvailableList(Long userId, AvailableCouponQueryDto query) {
        // 查询sku
        Set<Long> skuIds = query.getItems().stream().map(OrderPreviewItemDto::getSkuId).collect(Collectors.toSet());
        if (skuIds.size() != query.getItems().size()) {
            throw new BaseException("订单项中存在重复的SKU ID");
        }
        List<ProductSkuEntity> listSku = productSkuRepo.findListByIds(skuIds);
        if (listSku.size() != skuIds.size()) {
            throw new BaseException("订单项中存在无效的SKU ID");
        }
        Map<Long, Integer> mapSkuQuantity = query.getItems()
                .stream().collect(Collectors.toMap(OrderPreviewItemDto::getSkuId, OrderPreviewItemDto::getQuantity));

        // 检查：是否属于多家店铺
        Set<Long> shopIds = listSku.stream().map(ProductSkuEntity::getShopId).collect(Collectors.toSet());
        if (shopIds.size() > 1) {
            throw new BaseException("订单项中存在多家店铺的SKU");
        }

        // 查询spu
        Set<Long> spuIds = listSku.stream().map(ProductSkuEntity::getSpuId).collect(Collectors.toSet());
        Map<Long, ProductSpuEntity> mapSpu = productSpuRepo.findListByIds(spuIds).stream()
                .collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));
        if (mapSpu.size() != spuIds.size()) {
            throw new BaseException("订单项中存在无效的SPU ID");
        }

        final Long shopId = listSku.get(0).getShopId();
        final Map<Long, BigDecimal> mapSpuAmount = new HashMap<>();
        final Map<Long, BigDecimal> mapCategoryAmount = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ProductSkuEntity sku : listSku) {
            // 累加商品总金额
            Integer quantity = mapSkuQuantity.getOrDefault(sku.getId(), 0);
            BigDecimal itemAmount = sku.getPrice().multiply(BigDecimal.valueOf(quantity.longValue()));
            totalAmount = totalAmount.add(itemAmount);

            ProductSpuEntity spu = mapSpu.get(sku.getSpuId());
            // 分组累加商品金额
            mapSpuAmount.merge(spu.getId(), itemAmount, BigDecimal::add);
            // 分组累加分类金额
            mapCategoryAmount.merge(spu.getCategoryId(), itemAmount, BigDecimal::add);
        }

        // 先查询当前用户所有可用的红包
        List<MemberCouponVo> allAvailableCoupons = memberCouponRepo.queryReceiveList(userId, Page.of(1, 999999),
                CouponQueryDto.of(MemberCouponQueryConditionEnum.AVAILABLE.getIntValue())).getRecords();

        // 然后根据条件过滤出满足条件的红包列表
        BigDecimal finalTotalAmount = totalAmount;
        return allAvailableCoupons.stream()
                .filter(coupon -> {
                    // 判断优惠券类型
                    CouponTypeEnum couponType = CouponTypeEnum.of(coupon.getType());
                    if (couponType == null) {
                        log.error("优惠券类型错误：{}", coupon.getType());
                        return false;
                    }

                    // 判断使用范围
                    CouponUseRangeEnum useRange = CouponUseRangeEnum.of(coupon.getUseRange());
                    if (useRange == null) {
                        log.error("优惠券使用范围错误：{}", coupon.getUseRange());
                        return false;
                    }

                    // 使用范围对应的金额
                    BigDecimal scopeAmount;
                    if (useRange == CouponUseRangeEnum.SHOP) {
                        if (!Objects.equals(coupon.getTargetId(), shopId)) {
                            return false;
                        }
                        // 取店铺总金额
                        scopeAmount = finalTotalAmount;
                    } else if (useRange == CouponUseRangeEnum.PRODUCT) {
                        // 取指定商品总金额
                        scopeAmount = mapSpuAmount.get(coupon.getTargetId());
                    } else if (useRange == CouponUseRangeEnum.CATEGORY) {
                        // 取指定分类总金额
                        scopeAmount = mapCategoryAmount.get(coupon.getTargetId());
                    } else {
                        return false;
                    }

                    if (scopeAmount == null) {
                        log.error("优惠券使用范围对应的金额不存在，useRange={}, targetId={}",
                                useRange.getValue(), coupon.getTargetId());
                        return false;
                    }

                    if (scopeAmount.compareTo(coupon.getMinAmount()) < 0) {
                        return false;
                    }

                    // 优惠后金额不能小于0.01
                    BigDecimal discountAmount = couponType == CouponTypeEnum.DISCOUNT
                            ? scopeAmount.multiply(coupon.getDiscount())
                            : scopeAmount.subtract(coupon.getAmount());
                    discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
                    return discountAmount.compareTo(BigDecimal.ZERO) > 0;
                }).collect(Collectors.toList());
    }
}
