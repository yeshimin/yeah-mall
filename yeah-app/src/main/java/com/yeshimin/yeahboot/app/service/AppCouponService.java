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
import com.yeshimin.yeahboot.data.common.enums.MemberCouponReceiveChannelEnum;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.CouponQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import com.yeshimin.yeahboot.data.domain.vo.MemberCouponVo;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySkuRepo;
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
    private final GroupBuySkuRepo groupBuySkuRepo;

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
        // 会员领取记录用于限制单人领取次数，库存扣减由后续条件更新兜底。
        long receivedCount = memberCouponRepo.countByMemberIdAndCouponId(userId, couponId);
        // 检查：是否已达到领取上限
        if (receivedCount >= coupon.getPerLimit()) {
            throw new BaseException("已超过领取数量限制");
        }

        // 条件更新防止并发领取突破优惠券总量。
        int updateCount = mchCouponRepo.receiveCoupon(couponId, 1);
        if (updateCount == 0) {
            throw new BaseException("优惠券已领取完");
        }

        MemberCouponEntity memberCoupon =
                BeanUtil.copyProperties(coupon, MemberCouponEntity.class, CommonConsts.COPY_IGNORE_FIELDS);
        // 会员券保存领取时快照，后续商家改券不影响已领取优惠券规则。
        memberCoupon.setMemberId(userId);
        memberCoupon.setCouponId(coupon.getId());
        // 普通领券与积分兑换使用同一会员券资产，但必须记录来源渠道便于商家核对。
        memberCoupon.setReceiveChannel(MemberCouponReceiveChannelEnum.NORMAL_RECEIVE.getIntValue());
        memberCouponRepo.save(memberCoupon);
    }

    /**
     * 查询可使用的优惠券列表
     */
    public List<MemberCouponVo> queryAvailableList(Long userId, AvailableCouponQueryDto query) {
        // 订单预览只允许同店铺商品，优惠券的店铺范围才能确定。
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
            // 同时按SPU和类目累计金额，以匹配指定商品、指定类目和全店券。
            mapSpuAmount.merge(spu.getId(), itemAmount, BigDecimal::add);
            // 分组累加分类金额
            mapCategoryAmount.merge(spu.getCategoryId(), itemAmount, BigDecimal::add);
        }

        return filterAvailableCoupons(userId, shopId, mapSpuAmount, mapCategoryAmount, totalAmount, false);
    }

    /**
     * 查询拼团订单可用的优惠券列表
     */
    public List<MemberCouponVo> queryAvailableGroupBuyList(Long userId, AvailableCouponQueryDto query) {
        // 拼团请求中的SKU ID是拼团SKU，需映射回原商品后按原券使用范围判断。
        Set<Long> groupBuySkuIds = query.getItems().stream()
                .map(OrderPreviewItemDto::getSkuId).collect(Collectors.toSet());
        if (groupBuySkuIds.size() != query.getItems().size()) {
            throw new BaseException("订单项中存在重复的拼团SKU ID");
        }
        List<GroupBuySkuEntity> groupBuySkus = groupBuySkuRepo.findListByIds(groupBuySkuIds);
        if (groupBuySkus.size() != groupBuySkuIds.size()) {
            throw new BaseException("订单项中存在无效的拼团SKU ID");
        }
        Set<Long> shopIds = groupBuySkus.stream().map(GroupBuySkuEntity::getShopId).collect(Collectors.toSet());
        if (shopIds.size() > 1) {
            throw new BaseException("订单项中存在多家店铺的SKU");
        }
        Map<Long, Integer> quantityMap = query.getItems().stream()
                .collect(Collectors.toMap(OrderPreviewItemDto::getSkuId, OrderPreviewItemDto::getQuantity));
        Set<Long> sourceSkuIds = groupBuySkus.stream().map(GroupBuySkuEntity::getSkuId).collect(Collectors.toSet());
        Map<Long, ProductSkuEntity> sourceSkuMap = productSkuRepo.findListByIds(sourceSkuIds).stream()
                .collect(Collectors.toMap(ProductSkuEntity::getId, sku -> sku));
        Set<Long> spuIds = groupBuySkus.stream().map(GroupBuySkuEntity::getSpuId).collect(Collectors.toSet());
        Map<Long, ProductSpuEntity> spuMap = productSpuRepo.findListByIds(spuIds).stream()
                .collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));
        if (sourceSkuMap.size() != sourceSkuIds.size() || spuMap.size() != spuIds.size()) {
            throw new BaseException("原始商品信息不存在");
        }

        Map<Long, BigDecimal> spuAmount = new HashMap<>();
        Map<Long, BigDecimal> categoryAmount = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (GroupBuySkuEntity groupBuySku : groupBuySkus) {
            int quantity = quantityMap.getOrDefault(groupBuySku.getId(), 0);
            BigDecimal itemAmount = groupBuySku.getGroupPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemAmount);
            ProductSpuEntity spu = spuMap.get(groupBuySku.getSpuId());
            spuAmount.merge(spu.getId(), itemAmount, BigDecimal::add);
            categoryAmount.merge(spu.getCategoryId(), itemAmount, BigDecimal::add);
        }
        // 最后一个参数要求优惠券显式开启“支持拼团”才可返回。
        return filterAvailableCoupons(userId, groupBuySkus.get(0).getShopId(), spuAmount, categoryAmount, totalAmount, true);
    }

    /**
     * 按订单金额与使用范围筛选可用优惠券
     */
    private List<MemberCouponVo> filterAvailableCoupons(Long userId, Long shopId,
                                                         Map<Long, BigDecimal> mapSpuAmount,
                                                         Map<Long, BigDecimal> mapCategoryAmount,
                                                         BigDecimal totalAmount, boolean groupBuy) {
        // 先按会员券基础状态取候选集，再在内存中结合本次订单金额和范围精确筛选。
        List<MemberCouponVo> allAvailableCoupons = memberCouponRepo.queryReceiveList(userId, Page.of(1, 999999),
                CouponQueryDto.of(MemberCouponQueryConditionEnum.AVAILABLE.getIntValue())).getRecords();

        // 然后根据条件过滤出满足条件的红包列表
        BigDecimal finalTotalAmount = totalAmount;
        return allAvailableCoupons.stream()
                .filter(coupon -> {
                    if (groupBuy && !Boolean.TRUE.equals(coupon.getIsSupportGroupBuy())) {
                        // 拼团订单不默认兼容普通券，必须由商家显式授权。
                        return false;
                    }
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
