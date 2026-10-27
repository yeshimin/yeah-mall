package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.PointsExchangeDto;
import com.yeshimin.yeahboot.app.domain.vo.PointsProductVo;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.data.common.consts.PointsMallConsts;
import com.yeshimin.yeahboot.data.common.enums.MemberCouponReceiveChannelEnum;
import com.yeshimin.yeahboot.data.common.enums.MemberPointsBizTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsFulfillmentTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsOrderOperationEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsOrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.MemberAddressEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsCategoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderLogEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSpuEntity;
import com.yeshimin.yeahboot.data.repository.MemberAddressRepo;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.data.repository.PointsCategoryRepo;
import com.yeshimin.yeahboot.data.repository.PointsOrderLogRepo;
import com.yeshimin.yeahboot.data.repository.PointsOrderRepo;
import com.yeshimin.yeahboot.data.repository.PointsSkuRepo;
import com.yeshimin.yeahboot.data.repository.PointsSpuRepo;
import com.yeshimin.yeahboot.service.MemberPointsService;
import com.yeshimin.yeahboot.service.PointsMallConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * App积分商城服务
 */
@Service
@RequiredArgsConstructor
public class AppPointsMallService {

    private final PointsCategoryRepo categoryRepo;
    private final PointsSpuRepo spuRepo;
    private final PointsSkuRepo skuRepo;
    private final PointsOrderRepo orderRepo;
    private final PointsOrderLogRepo logRepo;
    private final MemberAddressRepo addressRepo;
    private final MchCouponRepo couponRepo;
    private final MemberCouponRepo memberCouponRepo;
    private final MemberPointsService pointsService;
    private final PointsMallConfigService configService;
    private final IdService idService;

    /**
     * 查询积分商品分类
     */
    public List<PointsCategoryEntity> categories() {
        configService.checkMallEnabled();
        return categoryRepo.lambdaQuery()
                .eq(PointsCategoryEntity::getIsEnabled, true)
                .orderByAsc(PointsCategoryEntity::getSort)
                .list();
    }

    /**
     * 分页查询可兑换商品
     */
    public IPage<PointsProductVo> products(Page<PointsSpuEntity> page, Long categoryId, String keyword) {
        configService.checkMallEnabled();
        LocalDateTime now = LocalDateTime.now();
        IPage<PointsSpuEntity> result = spuRepo.lambdaQuery()
                .eq(PointsSpuEntity::getIsEnabled, true)
                .eq(categoryId != null, PointsSpuEntity::getCategoryId, categoryId)
                .like(keyword != null && !keyword.trim().isEmpty(), PointsSpuEntity::getName, keyword)
                .le(PointsSpuEntity::getBeginTime, now)
                .ge(PointsSpuEntity::getEndTime, now)
                .orderByAsc(PointsSpuEntity::getSort)
                .orderByDesc(PointsSpuEntity::getCreateTime)
                .page(page);
        // 一次性预取当前页所有SKU，避免按商品逐条查询最低积分价和库存。
        Set<Long> spuIds = result.getRecords().stream()
                .map(PointsSpuEntity::getId)
                .collect(Collectors.toSet());
        Map<Long, List<PointsSkuEntity>> skuMap = spuIds.isEmpty()
                ? Collections.emptyMap()
                : skuRepo.lambdaQuery()
                        .in(PointsSkuEntity::getSpuId, spuIds)
                        .eq(PointsSkuEntity::getIsEnabled, true)
                        .list().stream()
                        .collect(Collectors.groupingBy(PointsSkuEntity::getSpuId));
        return result.convert(spu -> {
            PointsProductVo vo = BeanUtil.copyProperties(spu, PointsProductVo.class);
            List<PointsSkuEntity> skus = skuMap.getOrDefault(spu.getId(), Collections.emptyList());
            vo.setMinPointsPrice(skus.stream().map(PointsSkuEntity::getPointsPrice).min(Long::compareTo).orElse(0L));
            vo.setStock(skus.stream().map(PointsSkuEntity::getStock).filter(Objects::nonNull)
                    .reduce(0, Integer::sum));
            return vo;
        });
    }

    /**
     * 查询可兑换商品详情
     */
    public PointsSpuEntity detail(Long id) {
        configService.checkMallEnabled();
        PointsSpuEntity entity = spuRepo.getOneById(id, "积分商品不存在");
        validateExchangeTime(entity);
        return entity;
    }

    /**
     * 查询商品可兑换规格
     */
    public List<PointsSkuEntity> skus(Long spuId) {
        detail(spuId);
        return skuRepo.lambdaQuery()
                .eq(PointsSkuEntity::getSpuId, spuId)
                .eq(PointsSkuEntity::getIsEnabled, true)
                .orderByAsc(PointsSkuEntity::getSort)
                .list();
    }

    /**
     * 分页查询会员积分订单
     */
    public IPage<PointsOrderEntity> orders(Long memberId, Page<PointsOrderEntity> page, String status) {
        if (status != null && !status.trim().isEmpty() && PointsOrderStatusEnum.of(status) == null) {
            throw new BaseException("积分订单状态不正确");
        }
        return orderRepo.lambdaQuery()
                .eq(PointsOrderEntity::getMemberId, memberId)
                .eq(status != null && !status.trim().isEmpty(), PointsOrderEntity::getStatus, status)
                .orderByDesc(PointsOrderEntity::getCreateTime)
                .page(page);
    }

    /**
     * 查询会员积分订单详情
     */
    public PointsOrderEntity orderDetail(Long memberId, Long id) {
        PointsOrderEntity order = orderRepo.getOneById(id, "积分订单不存在");
        if (!Objects.equals(memberId, order.getMemberId())) {
            throw new BaseException("积分订单不存在");
        }
        return order;
    }

    /**
     * 兑换积分商品
     */
    @Transactional(rollbackFor = Exception.class)
    public PointsOrderEntity exchange(Long memberId, PointsExchangeDto dto) {
        configService.checkMallEnabled();
        // 同一会员的兑换串行处理，避免限兑数量与积分余额被并发突破。
        pointsService.lockAccount(memberId);
        PointsOrderEntity old = orderRepo.lambdaQuery()
                .eq(PointsOrderEntity::getMemberId, memberId)
                .eq(PointsOrderEntity::getRequestNo, dto.getRequestNo())
                .one();
        if (old != null) {
            return old;
        }

        // SKU 与 SPU 都必须处于可兑换状态，防止商品下架后仍可通过旧页面提交兑换。
        PointsSkuEntity sku = skuRepo.getOneById(dto.getSkuId(), "积分商品规格不存在");
        if (!Boolean.TRUE.equals(sku.getIsEnabled())) {
            throw new BaseException("积分商品规格已停用");
        }
        PointsSpuEntity spu = detail(sku.getSpuId());

        PointsFulfillmentTypeEnum fulfillmentType = PointsFulfillmentTypeEnum.of(spu.getFulfillmentType());
        if (fulfillmentType == null) {
            throw new BaseException("积分商品履约方式错误");
        }
        if (fulfillmentType == PointsFulfillmentTypeEnum.AUTO_COUPON
                && dto.getQuantity() != PointsMallConsts.AUTO_COUPON_EXCHANGE_QUANTITY) {
            throw new BaseException("优惠券每次只能兑换1张");
        }

        // 统计未取消订单数量，已取消兑换不占用会员的限兑额度。
        long exchanged = orderRepo.sumExchangedQuantity(memberId, spu.getId());
        if (spu.getPerMemberLimit() != null
                && spu.getPerMemberLimit() > PointsMallConsts.UNLIMITED_PER_MEMBER_LIMIT
                && exchanged + dto.getQuantity() > spu.getPerMemberLimit()) {
            throw new BaseException("超过每人限兑数量");
        }

        MemberAddressEntity address = null;
        if (fulfillmentType == PointsFulfillmentTypeEnum.LOGISTICS) {
            // 物流商品在下单时保存地址快照，后续会员修改地址不影响已兑换订单。
            address = addressRepo.getOneById(dto.getAddressId(), "请选择收货地址");
            if (!Objects.equals(memberId, address.getMemberId())) {
                throw new BaseException("收货地址无效");
            }
        }

        long totalPoints;
        try {
            totalPoints = Math.multiplyExact(sku.getPointsPrice(), dto.getQuantity().longValue());
        } catch (ArithmeticException exception) {
            throw new BaseException("兑换积分超出允许范围");
        }
        // 条件扣减库存是并发兜底；库存不足时不创建订单也不扣积分。
        if (!skuRepo.deductStock(sku.getId(), dto.getQuantity())) {
            throw new BaseException("积分商品库存不足");
        }

        // 先创建订单和初始日志，再扣积分；事务回滚时库存、订单和积分保持一致。
        PointsOrderEntity order = createOrder(memberId, dto, spu, sku, address, totalPoints);
        log(order, "", order.getStatus(), PointsOrderOperationEnum.CREATE.getDescription());
        pointsService.consume(memberId, totalPoints, MemberPointsBizTypeEnum.POINTS_EXCHANGE, order.getId(),
                order.getOrderNo(), "积分商品兑换");

        if (fulfillmentType == PointsFulfillmentTypeEnum.AUTO_COUPON) {
            // 自动发券成功后订单直接完成，不进入人工或物流履约队列。
            issueCoupon(memberId, spu, order);
            completePending(order, PointsOrderOperationEnum.COUPON_AUTO_FULFILL.getDescription());
        }
        return order;
    }

    /**
     * 会员取消待履约订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long memberId, Long id) {
        PointsOrderEntity order = orderDetail(memberId, id);
        if (PointsFulfillmentTypeEnum.AUTO_COUPON.getValue().equals(order.getFulfillmentType())) {
            throw new BaseException("自动发券订单不可取消");
        }

        LocalDateTime now = LocalDateTime.now();
        // 条件更新保证发货与取消并发时只有一个状态迁移成功。
        if (!orderRepo.cancelPending(order.getId(), now, PointsOrderOperationEnum.MEMBER_CANCEL.getDescription())) {
            throw new BaseException("当前订单不可取消");
        }
        // 取消成功后才回补库存和积分，避免重复补偿。
        skuRepo.restoreStock(order.getSkuId(), order.getQuantity());
        pointsService.refundConsume(memberId, order.getTotalPoints(), MemberPointsBizTypeEnum.POINTS_CANCEL,
                order.getId(), order.getOrderNo(), "取消积分兑换退还积分");
        order.setStatus(PointsOrderStatusEnum.CANCELED.getValue());
        order.setCancelTime(now);
        order.setCancelReason(PointsOrderOperationEnum.MEMBER_CANCEL.getDescription());
        log(order, PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue(), order.getStatus(),
                PointsOrderOperationEnum.MEMBER_CANCEL.getDescription());
    }

    /**
     * 会员确认收货
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long memberId, Long id) {
        PointsOrderEntity order = orderDetail(memberId, id);
        LocalDateTime now = LocalDateTime.now();
        // 仅已发货订单可确认收货，防止重复点击或跨状态确认。
        if (!orderRepo.completeShipped(order.getId(), now)) {
            throw new BaseException("当前订单不可确认收货");
        }
        order.setStatus(PointsOrderStatusEnum.COMPLETED.getValue());
        order.setCompleteTime(now);
        log(order, PointsOrderStatusEnum.SHIPPED.getValue(), order.getStatus(),
                PointsOrderOperationEnum.MEMBER_CONFIRM_RECEIPT.getDescription());
    }

    /**
     * 创建积分订单快照
     */
    private PointsOrderEntity createOrder(Long memberId, PointsExchangeDto dto, PointsSpuEntity spu,
                                           PointsSkuEntity sku, MemberAddressEntity address, long totalPoints) {
        PointsOrderEntity order = new PointsOrderEntity();
        order.setOrderNo(idService.nextEncodedId());
        order.setRequestNo(dto.getRequestNo());
        order.setMemberId(memberId);
        order.setSpuId(spu.getId());
        order.setSkuId(sku.getId());
        order.setProductName(spu.getName());
        order.setSkuName(sku.getName());
        order.setProductImage(sku.getMainImage() == null || sku.getMainImage().trim().isEmpty()
                ? spu.getMainImage() : sku.getMainImage());
        order.setProductType(spu.getProductType());
        order.setFulfillmentType(spu.getFulfillmentType());
        order.setQuantity(dto.getQuantity());
        order.setPointsPrice(sku.getPointsPrice());
        order.setTotalPoints(totalPoints);
        order.setStatus(PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue());
        if (address != null) {
            order.setReceiverName(address.getName());
            order.setReceiverContact(address.getContact());
            order.setReceiverFullAddress(address.getFullAddress());
        }
        order.insert();
        return order;
    }

    /**
     * 发放积分兑换优惠券
     */
    private void issueCoupon(Long memberId, PointsSpuEntity spu, PointsOrderEntity order) {
        MchCouponEntity coupon = couponRepo.getOneById(spu.getCouponId(), "优惠券不存在");
        LocalDateTime now = LocalDateTime.now();
        if (!Boolean.TRUE.equals(coupon.getIsEnabled())
                || !Boolean.TRUE.equals(coupon.getIsPointsExchangeEnabled())
                || coupon.getBeginTime() == null || coupon.getBeginTime().isAfter(now)
                || coupon.getEndTime() == null || coupon.getEndTime().isBefore(now)) {
            throw new BaseException("优惠券当前不可兑换");
        }

        // 积分兑换同样受商家券的单会员领取上限约束。
        long receivedCount = memberCouponRepo.countByMemberIdAndCouponId(memberId, coupon.getId());
        if (coupon.getPerLimit() != null && receivedCount >= coupon.getPerLimit()) {
            throw new BaseException("已超过该优惠券领取上限");
        }
        if (couponRepo.receiveCoupon(coupon.getId(), PointsMallConsts.AUTO_COUPON_EXCHANGE_QUANTITY) == 0) {
            throw new BaseException("优惠券库存不足");
        }

        // 创建会员券规则快照，商家后续编辑原券不影响已兑换的优惠券。
        MemberCouponEntity memberCoupon =
                BeanUtil.copyProperties(coupon, MemberCouponEntity.class, CommonConsts.COPY_IGNORE_FIELDS);
        memberCoupon.setMemberId(memberId);
        memberCoupon.setCouponId(coupon.getId());
        memberCoupon.setReceiveChannel(MemberCouponReceiveChannelEnum.POINTS_EXCHANGE.getIntValue());
        memberCoupon.setSourcePointsOrderId(order.getId());
        memberCouponRepo.save(memberCoupon);
        order.setMemberCouponId(memberCoupon.getId());
        order.updateById();
    }

    /**
     * 完成待履约订单
     */
    private void completePending(PointsOrderEntity order, String remark) {
        LocalDateTime now = LocalDateTime.now();
        // 自动发券与人工履约都从待履约进入已完成，条件更新防止重复写日志。
        if (!orderRepo.completePending(order.getId(), now, remark)) {
            throw new BaseException("积分订单状态已变化");
        }
        String from = order.getStatus();
        order.setStatus(PointsOrderStatusEnum.COMPLETED.getValue());
        order.setCompleteTime(now);
        order.setFulfillmentRemark(remark);
        log(order, from, order.getStatus(), remark);
    }

    /**
     * 校验积分商品兑换时间
     */
    private void validateExchangeTime(PointsSpuEntity entity) {
        if (!Boolean.TRUE.equals(entity.getIsEnabled())) {
            throw new BaseException("积分商品已下架");
        }
        LocalDateTime now = LocalDateTime.now();
        if (entity.getBeginTime() == null || entity.getBeginTime().isAfter(now)) {
            throw new BaseException("积分商品兑换尚未开始");
        }
        if (entity.getEndTime() == null || entity.getEndTime().isBefore(now)) {
            throw new BaseException("积分商品兑换已结束");
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
