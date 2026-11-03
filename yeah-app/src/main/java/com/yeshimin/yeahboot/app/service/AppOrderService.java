package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.yeshimin.yeahboot.app.common.enums.OrderAggreStatusEnum;
import com.yeshimin.yeahboot.app.common.enums.OrderSceneEnum;
import com.yeshimin.yeahboot.app.domain.dto.*;
import com.yeshimin.yeahboot.app.domain.vo.*;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.common.utils.WxPayUtils;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.*;
import com.yeshimin.yeahboot.data.common.properties.SeckillProperties;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.domain.vo.OrderShopProductVo;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecOptVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillActivityCacheVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSkuCacheVo;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.service.JuheExpService;
import com.yeshimin.yeahboot.service.MemberPointsService;
import com.yeshimin.yeahboot.service.GroupBuyImageService;
import com.yeshimin.yeahboot.service.GroupBuyTradeService;
import com.yeshimin.yeahboot.service.OrderService;
import com.yeshimin.yeahboot.service.PaymentChannelService;
import com.yeshimin.yeahboot.service.ResolvedPaymentChannel;
import com.yeshimin.yeahboot.service.ShopSettlementService;
import com.yeshimin.yeahboot.service.WxPayInfoVo;
import com.yeshimin.yeahboot.service.WxPayService;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * App订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppOrderService {

    // 微信支付成功回调事件类型
    private static final String WX_PAY_NOTIFY_EVENT_TYPE = "TRANSACTION.SUCCESS";

    private final ProductSkuRepo productSkuRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ProductCategoryRepo productCategoryRepo;
    private final OrderItemRepo orderItemRepo;
    private final OrderRepo orderRepo;
    private final ShopRepo shopRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final CartItemRepo cartItemRepo;
    private final MemberAddressRepo memberAddressRepo;
    private final OrderDeliveryTrackingRepo orderDeliveryTrackingRepo;
    private final DeliveryProviderRepo deliveryProviderRepo;
    private final OrderRefundImageRepo orderRefundImageRepo;
    private final MemberRepo memberRepo;
    private final SeckillSkuRepo seckillSkuRepo;
    private final SeckillSpuRepo seckillSpuRepo;
    private final SeckillActivityRepo seckillActivityRepo;
    private final SeckillSessionRepo seckillSessionRepo;
    private final GroupBuyActivityRepo groupBuyActivityRepo;
    private final GroupBuySpuRepo groupBuySpuRepo;
    private final GroupBuySkuRepo groupBuySkuRepo;
    private final GroupBuyGroupRepo groupBuyGroupRepo;
    private final GroupBuyOrderRepo groupBuyOrderRepo;
    private final MemberCouponRepo memberCouponRepo;
    private final MchCouponRepo mchCouponRepo;
    private final OrderCouponRepo orderCouponRepo;

    private final IdService idService;
    private final JuheExpService juheExpService;

    private final WxPayService wxPayService;
    private final PaymentChannelService paymentChannelService;
    private final OrderService orderService;
    private final GroupBuyImageService groupBuyImageService;
    private final GroupBuyTradeService groupBuyTradeService;
    private final CacheService cacheService;
    private final MemberPointsService memberPointsService;
    private final ShopSettlementService shopSettlementService;
    private final MallAppContextService mallAppContextService;
    private final StorageManager storageManager;

    /**
     * 订单支付超时时间（分钟）
     */
    @Value("${order-pay-timeout-minutes:30}")
    private Integer orderPayTimeoutMinutes;

    private final SeckillProperties seckillProperties;

    /**
     * 提交订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderEntity submit(Long userId, OrderSubmitDto dto) {
        // 检查：收货地址
        MemberAddressEntity address = memberAddressRepo.findOneById(dto.getAddressId());
        if (address == null || !Objects.equals(address.getMemberId(), userId)) {
            throw new BaseException("收货地址无效");
        }

        // 查询sku
        Set<Long> skuIds = dto.getItems().stream().map(OrderItemDto::getSkuId).collect(Collectors.toSet());
        if (skuIds.size() != dto.getItems().size()) {
            throw new BaseException("订单项中存在重复的SKU ID");
        }
        List<ProductSkuEntity> listSku = productSkuRepo.findListByIds(skuIds);
        if (listSku.size() != skuIds.size()) {
            throw new BaseException("订单项中存在无效的SKU ID");
        }

        // 检查：是否属于多家店铺
        Set<Long> shopIds = listSku.stream().map(ProductSkuEntity::getShopId).collect(Collectors.toSet());
        if (shopIds.size() > 1) {
            throw new BaseException("订单项中存在多家店铺的SKU");
        }
        this.checkShopScope(shopIds);

        // 查询spu
        Set<Long> spuIds = listSku.stream().map(ProductSkuEntity::getSpuId).collect(Collectors.toSet());
        Map<Long, ProductSpuEntity> mapSpu = productSpuRepo.findListByIds(spuIds)
                .stream().collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));
        if (mapSpu.size() != spuIds.size()) {
            throw new BaseException("订单项中存在无效的SPU ID");
        }
        this.checkProductsEnabled(mapSpu.values());

        // 创建订单
        OrderEntity order = new OrderEntity();
        List<OrderItemEntity> orderItems = new ArrayList<>(dto.getItems().size());

        // 生成订单编号
        final String orderNo = idService.nextEncodedId();

        // 总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<Long, BigDecimal> mapSpuAmount = new HashMap<>();
        Map<Long, BigDecimal> mapCategoryAmount = new HashMap<>();

        // 生成订单明细
        for (OrderItemDto item : dto.getItems()) {
            ProductSkuEntity sku = listSku.stream()
                    .filter(s -> s.getId().equals(item.getSkuId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException("SKU ID " + item.getSkuId() + " 不存在"));
            ProductSpuEntity spu = mapSpu.get(sku.getSpuId());
            if (spu == null) {
                throw new BaseException("SPU ID " + sku.getSpuId() + " 不存在");
            }

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setMchId(sku.getMchId());
            orderItem.setShopId(sku.getShopId());
            orderItem.setMemberId(userId);
            orderItem.setOrderNo(orderNo);
            orderItem.setSkuId(sku.getId());
            orderItem.setSkuName(sku.getName());
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setSpuName(spu.getName());
            orderItem.setSpuMainImage(spu.getMainImage());
            orderItem.setUnitPrice(sku.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(sku.getPrice().multiply(new BigDecimal(item.getQuantity())));
            orderItems.add(orderItem);

            // 累加总金额
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            mapSpuAmount.merge(spu.getId(), orderItem.getTotalPrice(), BigDecimal::add);
            mapCategoryAmount.merge(spu.getCategoryId(), orderItem.getTotalPrice(), BigDecimal::add);
        }
        this.mergeParentCategoryAmount(mapCategoryAmount);

        // 检查：用户优惠券
        Long shopId = listSku.get(0).getShopId();
        CouponApplyResult couponResult = this.checkCouponAvailable(
                dto.getCouponId(), userId, shopId, mapSpuAmount, mapCategoryAmount, totalAmount, false);
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount;

        // 先占用优惠券，避免并发重复下单
        if (couponResult != null) {
            boolean updated = memberCouponRepo.occurCoupon(couponResult.memberCoupon.getId());
            if (!updated) {
                throw new BaseException("优惠券已被使用");
            }
            if (!mchCouponRepo.increaseUsed(couponResult.memberCoupon.getCouponId())) {
                throw new BaseException("优惠券占用失败");
            }
            discountAmount = couponResult.discountAmount;
            finalAmount = totalAmount.subtract(discountAmount);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
        }

        // 检查库存
        for (OrderItemDto item : dto.getItems()) {
            ProductSkuEntity sku = listSku.stream()
                    .filter(s -> s.getId().equals(item.getSkuId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException("SKU ID " + item.getSkuId() + " 不存在"));

            if (sku.getStock() < item.getQuantity()) {
                throw new BaseException("SKU " + sku.getName() + " 库存不足");
            }

            boolean r = productSkuRepo.occurStock(item.getSkuId(), item.getQuantity());
            log.info("sku[{},{}] 扣减库存结果：{}", sku.getId(), sku.getName(), r);
            if (!r) {
                throw new BaseException("SKU " + sku.getName() + " 库存不足");
            }
        }

        // 查询买家信息
        MemberEntity member = memberRepo.getOneById(userId);

        // 保存订单
        Optional.ofNullable(listSku.get(0)).ifPresent(sku -> {
            order.setMchId(sku.getMchId());
            order.setShopId(sku.getShopId());
        });
        order.setMemberId(userId);
        order.setMemberNickname(member.getNickname());
        order.setMemberAvatar(member.getAvatar());
        order.setOrderNo(orderNo);
        order.setBuyerRemark(Optional.ofNullable(dto.getBuyerRemark()).map(String::trim).orElse(""));
        order.setOrderType(OrderTypeEnum.NORMAL.getIntValue()); // 设置订单类型
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        this.initializePaymentSnapshot(order, mallAppContextService.getAppTarget());
        order.setStatus(OrderStatusEnum.WAIT_PAY.getValue()); // 设置订单状态为待付款
        // 计算并设置支付超时时间
        order.setPayExpireTime(this.calcOrderPayExpireTime());
        // 保存买家收货地址信息
        this.setOrderReceiverAddress(order, address);
        order.insert();
        // 保存订单明细
        orderItems.forEach(orderItem -> orderItem.setOrderId(order.getId()));
        orderItemRepo.saveBatch(orderItems);

        // 保存订单优惠券快照
        if (couponResult != null) {
            orderCouponRepo.createOne(order, couponResult.memberCoupon);
        }

        // 如果是【购物车下单】场景，则清除对应的购物车商品
        if (Objects.equals(dto.getScene(), OrderSceneEnum.CART.getValue())) {
            if (!skuIds.isEmpty()) {
                cartItemRepo.deleteByMemberIdAndSkuIds(userId, skuIds);
            }
        }

        return order;
    }

    /**
     * 提交拼团订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderEntity submitForGroupBuy(Long userId, GroupBuyOrderSubmitDto dto) {
        // 拼团按商品SPU成团，每个成员每次仅能购买一个拼团SKU。
        if (dto.getItems().size() != 1 || dto.getItems().get(0).getQuantity() != 1
                || !Objects.equals(dto.getItems().get(0).getSkuId(), dto.getGroupBuySkuId())) {
            throw new BaseException("拼团订单参数错误");
        }
        if (!Objects.equals(dto.getScene(), OrderSceneEnum.GROUP_BUY.getValue())) {
            throw new BaseException("订单场景参数错误");
        }

        MemberAddressEntity address = memberAddressRepo.findOneById(dto.getAddressId());
        if (address == null || !Objects.equals(address.getMemberId(), userId)) {
            throw new BaseException("收货地址无效");
        }

        GroupBuySkuEntity groupBuySku = groupBuySkuRepo.getOneById(dto.getGroupBuySkuId(), "拼团商品不存在");
        mallAppContextService.checkShopScope(groupBuySku.getShopId());
        GroupBuySpuEntity groupBuySpu = groupBuySpuRepo.getOneById(groupBuySku.getGroupBuySpuId(), "拼团商品不存在");
        GroupBuyActivityEntity activity = groupBuyActivityRepo.getOneById(groupBuySpu.getActivityId(), "拼团活动不存在");
        LocalDateTime now = LocalDateTime.now();
        // 下单时再次校验活动开关、人工状态和时间，不能只依赖商品详情页的前端限制。
        if (!Boolean.TRUE.equals(activity.getIsEnabled())
                || !GroupBuyActivityStatusEnum.ACTIVITY_STARTED.equalsValue(activity.getStatus())
                || now.isBefore(activity.getActivityBeginTime()) || now.isAfter(activity.getActivityEndTime())) {
            throw new BaseException("拼团活动未开始或已结束");
        }
        if (!Objects.equals(groupBuySku.getActivityId(), activity.getId())
                || !Objects.equals(groupBuySpu.getAuditStatus(), GroupBuyApplyAuditStatusEnum.PASSED.getIntValue())) {
            throw new BaseException("拼团商品不可购买");
        }

        ProductSkuEntity sourceSku = productSkuRepo.getOneById(groupBuySku.getSkuId(), "原始商品不存在");
        ProductSpuEntity sourceSpu = productSpuRepo.getOneById(groupBuySku.getSpuId(), "原始商品不存在");
        // 锁定会员行，串行化同一会员的拼团下单，避免并发请求加入多个团。
        MemberEntity member = memberRepo.findOneByIdForUpdate(userId);
        if (member == null) {
            throw new BaseException("会员不存在");
        }
        GroupBuyGroupEntity group = this.prepareGroup(userId, dto.getGroupId(), Boolean.TRUE.equals(dto.getRandomJoin()),
                activity, groupBuySpu);

        // 优惠券仍按原始商品和类目判定范围，但以拼团价作为订单金额进行折扣计算。
        BigDecimal totalAmount = groupBuySku.getGroupPrice();
        Map<Long, BigDecimal> spuAmount = Collections.singletonMap(sourceSpu.getId(), totalAmount);
        Map<Long, BigDecimal> categoryAmount = new HashMap<>();
        categoryAmount.put(sourceSpu.getCategoryId(), totalAmount);
        this.mergeParentCategoryAmount(categoryAmount);
        CouponApplyResult couponResult = this.checkCouponAvailable(dto.getCouponId(), userId, groupBuySku.getShopId(),
                spuAmount, categoryAmount, totalAmount, true);
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount;
        if (couponResult != null) {
            // 条件更新占用会员券，避免并发下单重复使用同一张券。
            boolean occurred = memberCouponRepo.occurCoupon(couponResult.memberCoupon.getId());
            if (!occurred) {
                throw new BaseException("优惠券已被使用");
            }
            if (!mchCouponRepo.increaseUsed(couponResult.memberCoupon.getCouponId())) {
                throw new BaseException("优惠券占用失败");
            }
            discountAmount = couponResult.discountAmount;
            finalAmount = totalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
        }

        if (!groupBuySkuRepo.occurStock(groupBuySku.getId(), 1)) {
            throw new BaseException("拼团商品库存不足");
        }

        // 主订单沿用通用订单表；付款后先进入待成团，活动结算后才能待发货。
        String orderNo = idService.nextEncodedId();
        OrderEntity order = new OrderEntity();
        order.setMchId(groupBuySku.getMchId());
        order.setShopId(groupBuySku.getShopId());
        order.setMemberId(userId);
        order.setMemberNickname(member.getNickname());
        order.setMemberAvatar(member.getAvatar());
        order.setOrderNo(orderNo);
        order.setBuyerRemark(Optional.ofNullable(dto.getBuyerRemark()).map(String::trim).orElse(""));
        order.setOrderType(OrderTypeEnum.GROUP_BUY.getIntValue());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        this.initializePaymentSnapshot(order, mallAppContextService.getAppTarget());
        order.setStatus(OrderStatusEnum.WAIT_PAY.getValue());
        order.setPayExpireTime(this.calcOrderPayExpireTime());
        this.setOrderReceiverAddress(order, address);
        order.insert();

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setMchId(groupBuySku.getMchId());
        orderItem.setShopId(groupBuySku.getShopId());
        orderItem.setMemberId(userId);
        orderItem.setOrderId(order.getId());
        orderItem.setOrderNo(orderNo);
        // 订单明细保留原始SKU，保证订单详情、规格与售后仍可复用通用逻辑。
        orderItem.setSkuId(sourceSku.getId());
        orderItem.setSkuName(groupBuySku.getName());
        orderItem.setSpuId(sourceSpu.getId());
        orderItem.setSpuName(groupBuySpu.getName());
        orderItem.setSpuMainImage(groupBuyImageService.resolveSpuMainImage(groupBuySpu));
        orderItem.setUnitPrice(groupBuySku.getGroupPrice());
        orderItem.setQuantity(1);
        orderItem.setTotalPrice(totalAmount);
        orderItem.insert();

        if (couponResult != null) {
            // 保存订单与会员券关联，自动退款或取消时据此释放优惠券。
            orderCouponRepo.createOne(order, couponResult.memberCoupon);
        }

        // 拼团订单关联表承载团、活动、拼团SKU和参与状态，不能只依赖主订单类型推断。
        GroupBuyOrderEntity groupBuyOrder = new GroupBuyOrderEntity();
        groupBuyOrder.setMchId(groupBuySku.getMchId());
        groupBuyOrder.setShopId(groupBuySku.getShopId());
        groupBuyOrder.setGroupId(group.getId());
        groupBuyOrder.setActivityId(activity.getId());
        groupBuyOrder.setGroupBuySpuId(groupBuySpu.getId());
        groupBuyOrder.setGroupBuySkuId(groupBuySku.getId());
        groupBuyOrder.setMemberId(userId);
        groupBuyOrder.setOrderId(order.getId());
        groupBuyOrder.setOrderNo(orderNo);
        groupBuyOrder.setIsLeader(Objects.equals(group.getLeaderMemberId(), userId));
        groupBuyOrder.setStatus(GroupBuyOrderStatusEnum.WAIT_PAY.getValue());
        groupBuyOrder.insert();
        return order;
    }

    /**
     * 秒杀
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderSeckillVo seckill(Long userId, SeckillDto dto) {
        Long skuId = dto.getSkuId();
        SeckillSkuEntity seckillSku = seckillSkuRepo.getOneById(skuId, "秒杀商品不存在");
        mallAppContextService.checkShopTradeAvailable(seckillSku.getShopId());

        OrderSeckillVo result = new OrderSeckillVo();
        result.setSkuId(skuId);

        // 尝试秒杀占用库存
        long remainStock = this.trySeckill(userId, skuId, seckillSku.getSeckillSpuId());
        log.info("用户[{}]秒杀sku[{}]，剩余库存：{}", userId, skuId, remainStock);
        if (remainStock == -1) {
            result.setSuccess(false);
            result.setMessage("秒杀失败，商品已售罄");
            return result;
        }
        if (remainStock == -2) {
            result.setSuccess(false);
            result.setMessage("秒杀失败，您已经购买过了");
            return result;
        }

        // 抢购名额成功，记录抢购时间
        String seckillEventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, userId);
        SeckillEventCacheVo seckillEvent = new SeckillEventCacheVo();
        seckillEvent.setAttemptId(idService.nextEncodedId());
        seckillEvent.setQuotaTime(LocalDateTime.now());
        cacheService.set(seckillEventKey, JSON.toJSONString(seckillEvent));
        // 新一轮抢购不能读取到上一轮异步处理结果。
        cacheService.delete(String.format(BizConsts.SECKILL_RESULT_KEY, skuId, userId));

        result.setSuccess(true);
        result.setMessage("秒杀抢购成功，请尽快完成订单支付");
        return result;
    }

    /**
     * 校验秒杀商品是否属于当前商城
     */
    public void checkSeckillShopScope(Long skuId) {
        SeckillSkuEntity seckillSku = seckillSkuRepo.getOneById(skuId, "秒杀商品不存在");
        mallAppContextService.checkShopTradeAvailable(seckillSku.getShopId());
    }

    /**
     * 查询秒杀活动实时缓存，兼容升级前已开始的活动
     */
    public SeckillActivityCacheVo getSeckillActivityCache(Long activityId) {
        String key = String.format(BizConsts.SECKILL_ACTIVITY_KEY, activityId);
        SeckillActivityCacheVo result = cacheService.get(key, SeckillActivityCacheVo.class);
        if (result != null && result.getStatus() != null && result.getIsEnabled() != null) {
            return result;
        }

        SeckillActivityEntity activity = seckillActivityRepo.getOneById(activityId, "秒杀活动不存在");
        result = BeanUtil.copyProperties(activity, SeckillActivityCacheVo.class);
        cacheService.set(key, JSON.toJSONString(result));
        return result;
    }

    /**
     * 查询秒杀SKU活动与场次缓存，兼容升级前已开始的活动
     */
    public SeckillSkuCacheVo getSeckillSkuCache(Long skuId) {
        String key = String.format(BizConsts.SECKILL_SKU_CONTEXT_KEY, skuId);
        SeckillSkuCacheVo result = cacheService.get(key, SeckillSkuCacheVo.class);
        if (result != null) {
            return result;
        }

        SeckillSkuEntity sku = seckillSkuRepo.getOneById(skuId, "秒杀商品不存在");
        SeckillSessionEntity session = seckillSessionRepo.getOneById(sku.getSessionId(), "秒杀场次不存在");
        result = new SeckillSkuCacheVo();
        result.setSkuId(sku.getId());
        result.setSeckillSpuId(sku.getSeckillSpuId());
        result.setActivityId(sku.getActivityId());
        result.setSessionId(session.getId());
        result.setSessionBeginTime(session.getBeginTime());
        result.setSessionEndTime(session.getEndTime());
        cacheService.set(key, JSON.toJSONString(result));
        return result;
    }

    /**
     * 查询秒杀结果
     */
    @Transactional(rollbackFor = Exception.class)
    public SeckillBizResultVo querySeckillResult(Long userId, Long skuId) {
        String key = String.format(BizConsts.SECKILL_RESULT_KEY, skuId, userId);
        return cacheService.get(key, SeckillBizResultVo.class);
    }

    /**
     * 提交订单 for 秒杀场景
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderEntity submitForSeckill(Long userId, OrderSubmitDto dto, Long contextShopId, String contextAppTarget) {
        // 检查：收货地址
        MemberAddressEntity address = memberAddressRepo.findOneById(dto.getAddressId());
        if (address == null || !Objects.equals(address.getMemberId(), userId)) {
            throw new BaseException("收货地址无效");
        }

        // 查询sku
        Set<Long> skuIds = dto.getItems().stream().map(OrderItemDto::getSkuId).collect(Collectors.toSet());
        if (skuIds.size() != dto.getItems().size()) {
            throw new BaseException("订单项中存在重复的SKU ID");
        }
        List<SeckillSkuEntity> listSku = seckillSkuRepo.findListByIds(skuIds);
        if (listSku.size() != skuIds.size()) {
            throw new BaseException("订单项中存在无效的SKU ID");
        }
        // 检查：是否属于多家店铺
        Set<Long> shopIds = listSku.stream().map(SeckillSkuEntity::getShopId).collect(Collectors.toSet());
        if (shopIds.size() > 1) {
            throw new BaseException("订单项中存在多家店铺的SKU");
        }
        if (contextShopId != null && (shopIds.size() != 1 || !shopIds.contains(contextShopId))) {
            throw new BaseException("当前商城不可购买其他店铺商品");
        }

        // 检查库存
        for (OrderItemDto item : dto.getItems()) {
            SeckillSkuEntity sku = listSku.stream()
                    .filter(s -> s.getId().equals(item.getSkuId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException("SKU ID " + item.getSkuId() + " 不存在"));

            if (sku.getStock() < item.getQuantity()) {
                throw new BaseException("SKU " + sku.getName() + " 库存不足");
            }

            // 扣减库存
            boolean r = seckillSkuRepo.occurStock(item.getSkuId(), item.getQuantity());
            log.info("sku[{},{}] 扣减库存结果：{}", sku.getId(), sku.getName(), r);
            if (!r) {
                throw new BaseException("SKU " + sku.getName() + " 库存不足");
            }
        }

        // 创建订单
        OrderEntity order = new OrderEntity();
        List<OrderItemEntity> orderItems = new ArrayList<>(dto.getItems().size());

        // 生成订单编号
        final String orderNo = idService.nextEncodedId();
        // 查询spu
        Set<Long> spuIds = listSku.stream().map(SeckillSkuEntity::getSeckillSpuId).collect(Collectors.toSet());
        Map<Long, SeckillSpuEntity> mapSpu = seckillSpuRepo.findListByIds(spuIds)
                .stream().collect(Collectors.toMap(SeckillSpuEntity::getId, spu -> spu));

        // 总金额
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 生成订单明细
        for (OrderItemDto item : dto.getItems()) {
            SeckillSkuEntity sku = listSku.stream()
                    .filter(s -> s.getId().equals(item.getSkuId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException("SKU ID " + item.getSkuId() + " 不存在"));
            SeckillSpuEntity spu = mapSpu.get(sku.getSeckillSpuId());
            if (spu == null) {
                throw new BaseException("SPU ID " + sku.getSpuId() + " 不存在");
            }

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setMchId(sku.getMchId());
            orderItem.setShopId(sku.getShopId());
            orderItem.setMemberId(userId);
            orderItem.setOrderNo(orderNo);
            orderItem.setSkuId(sku.getId());
            orderItem.setSkuName(sku.getName());
            orderItem.setSpuId(sku.getSeckillSpuId()); // 这里用秒杀spuId
            orderItem.setSpuName(spu.getName());
            orderItem.setSpuMainImage(spu.getMainImage());
            orderItem.setUnitPrice(sku.getSeckillPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(sku.getSeckillPrice().multiply(new BigDecimal(item.getQuantity())));
            orderItems.add(orderItem);

            // 累加总金额
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }

        // 查询买家信息
        MemberEntity member = memberRepo.getOneById(userId);

        // 保存订单
        Optional.ofNullable(listSku.get(0)).ifPresent(sku -> {
            order.setMchId(sku.getMchId());
            order.setShopId(sku.getShopId());
        });
        order.setMemberId(userId);
        order.setMemberNickname(member.getNickname());
        order.setMemberAvatar(member.getAvatar());
        order.setOrderNo(orderNo);
        order.setBuyerRemark(Optional.ofNullable(dto.getBuyerRemark()).map(String::trim).orElse(""));
        order.setOrderType(OrderTypeEnum.SECKILL.getIntValue()); // 设置订单类型
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFinalAmount(totalAmount);
        this.initializePaymentSnapshot(order, contextAppTarget);
        order.setStatus(OrderStatusEnum.WAIT_PAY.getValue()); // 设置订单状态为待付款
        // 计算并设置支付超时时间
        order.setPayExpireTime(this.calcOrderPayExpireTimeForSeckill()); // 秒杀场景
        // 保存买家收货地址信息
        this.setOrderReceiverAddress(order, address);
        order.insert();
        // 保存订单明细
        orderItems.forEach(orderItem -> orderItem.setOrderId(order.getId()));
        orderItemRepo.saveBatch(orderItems);

        // 如果是【购物车下单】场景，则清除对应的购物车商品
        // NOTE 秒杀场景暂不需要这个逻辑

        return order;
    }

    /**
     * 生成支付信息
     */
    @Transactional(rollbackFor = Exception.class)
    public WxPayInfoVo genPayInfo(Long userId, Long orderId) {
        return this.genPayInfo(userId, orderId, true);
    }

    /**
     * 异步任务生成支付信息
     */
    @Transactional(rollbackFor = Exception.class)
    public WxPayInfoVo genPayInfoForAsync(Long userId, Long orderId) {
        return this.genPayInfo(userId, orderId, false);
    }

    /**
     * 校验订单并生成微信支付调起参数
     */
    private WxPayInfoVo genPayInfo(Long userId, Long orderId, boolean checkMallScope) {
        // 查询订单
        OrderEntity order = orderRepo.findOneById(orderId);
        if (order == null) {
            throw new BaseException("订单未找到");
        }
        if (!Objects.equals(order.getMemberId(), userId)) {
            throw new BaseException("无该订单权限");
        }
        if (checkMallScope) {
            this.checkOrderScope(order);
        }
        // 已支付、已关闭或超过支付截止时间的订单不能再次拉起收银台。
        if (!OrderStatusEnum.WAIT_PAY.equalsValue(order.getStatus())) {
            throw new BaseException("仅待付款订单可以发起支付");
        }
        if (order.getPayExpireTime() != null && !LocalDateTime.now().isBefore(order.getPayExpireTime())) {
            throw new BaseException("订单已超过支付时间");
        }

        String prepayId;
        // 如果还没有预支付订单，则创建一个
        if (StrUtil.isBlank(order.getWxPrepayId())) {
            prepayId = wxPayService.prepay(order, "商品描述" + System.currentTimeMillis());
            // 更新
            order.setWxPrepayId(prepayId);
            order.updateById();
        } else {
            prepayId = order.getWxPrepayId();
        }

        return wxPayService.genWxPayInfo(order, prepayId);
    }

    /**
     * 查询订单支付结果
     */
    public OrderPayResultVo queryPayResult(Long userId, Long orderId) {
        OrderEntity order = orderRepo.findOneById(orderId);
        return this.buildPayResult(userId, order);
    }

    /**
     * 按订单号查询支付结果，兼容旧版App支付结果页。
     */
    public OrderPayResultVo queryPayResultByOrderNo(Long userId, String orderNo) {
        OrderEntity order = orderRepo.findOneByOrderNo(orderNo);
        return this.buildPayResult(userId, order);
    }

    /**
     * 校验订单归属并构造支付结果。
     */
    private OrderPayResultVo buildPayResult(Long userId, OrderEntity order) {
        if (order == null) {
            throw new BaseException("订单未找到");
        }
        if (!Objects.equals(order.getMemberId(), userId)) {
            throw new BaseException("无该订单权限");
        }
        this.checkOrderScope(order);
        // 本地仍为待付款时主动查单，补偿微信回调暂未到达的情况。
        order = this.reconcilePayResult(order);

        OrderPayResultVo vo = new OrderPayResultVo();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setPaySuccess(order.getPaySuccessTime() != null);
        vo.setPayAmount(order.getPayAmount());
        vo.setPaySuccessTime(order.getPaySuccessTime());
        return vo;
    }

    /**
     * 回调尚未到达时主动查询微信订单并同步支付结果
     */
    private OrderEntity reconcilePayResult(OrderEntity order) {
        if (!OrderStatusEnum.WAIT_PAY.equalsValue(order.getStatus())
                || StrUtil.isBlank(order.getWxPrepayId())) {
            return order;
        }
        try {
            Transaction transaction = wxPayService.queryOrderByOutTradeNo(order);
            if (transaction != null && transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS
                    && orderService.handlePaySuccessByQuery(transaction)) {
                if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
                    groupBuyTradeService.handleOrderPaid(order.getId());
                }
                OrderEntity updated = orderRepo.findOneById(order.getId());
                OrderEntity result = updated == null ? order : updated;
                this.markSeckillPaySuccess(result);
                return result;
            }
        } catch (Exception e) {
            log.warn("主动同步支付结果失败，订单ID：{}，异常类型：{}",
                    order.getId(), e.getClass().getSimpleName());
        }
        return order;
    }

    /**
     * 查询个人订单
     */
    public IPage<OrderListVo> query(Long userId, Page<OrderEntity> page, OrderQueryDto dto) {
        OrderAggreStatusEnum aggreStatus = OrderAggreStatusEnum.of(dto.getAggreStatus());
        if (aggreStatus == null) {
            // 未指定聚合状态时仅应用基础查询条件和当前App店铺范围。
            LambdaQueryWrapper<OrderEntity> wrapper = QueryHelper.getQueryWrapper(dto, OrderEntity.class);
            wrapper.eq(OrderEntity::getMemberId, userId);
            this.applyShopScope(wrapper);
            wrapper.orderByDesc(OrderEntity::getCreateTime, OrderEntity::getId);
            return this.wrapOrderListVo(orderRepo.page(page, wrapper));
        }

        if (aggreStatus.isEqualMode()) {
            // 单状态聚合直接映射到对应订单状态。
            LambdaQueryWrapper<OrderEntity> wrapper = QueryHelper.getQueryWrapper(dto, OrderEntity.class);
            wrapper.eq(OrderEntity::getMemberId, userId);
            this.applyShopScope(wrapper);
            wrapper.eq(OrderEntity::getStatus, aggreStatus.toOrderStatusEnum().getValue());
            wrapper.orderByDesc(OrderEntity::getCreateTime, OrderEntity::getId);
            return this.wrapOrderListVo(orderRepo.page(page, wrapper));
        } else if (aggreStatus.isInMode()) {
            // 售后聚合包含退款和其他售后相关状态。
            LambdaQueryWrapper<OrderEntity> wrapper = QueryHelper.getQueryWrapper(dto, OrderEntity.class);
            wrapper.eq(OrderEntity::getMemberId, userId);
            this.applyShopScope(wrapper);
            wrapper.in(OrderEntity::getStatus, OrderStatusEnum.REFUND_AND_AFTER_SALE_STATUS);
            wrapper.orderByDesc(OrderEntity::getCreateTime, OrderEntity::getId);
            return this.wrapOrderListVo(orderRepo.page(page, wrapper));
        } else if (aggreStatus.isExtendMode()) {
            // 待评价是业务扩展状态，需要组合多个已完成订单状态。
            LambdaQueryWrapper<OrderEntity> wrapper = QueryHelper.getQueryWrapper(dto, OrderEntity.class);
            wrapper.eq(OrderEntity::getMemberId, userId);
            this.applyShopScope(wrapper);
            wrapper.in(OrderEntity::getStatus, OrderStatusEnum.WAIT_REVIEW_STATUS);
            wrapper.orderByDesc(OrderEntity::getCreateTime, OrderEntity::getId);
            return this.wrapOrderListVo(orderRepo.page(page, wrapper));
        } else {
            throw new BaseException("订单聚合状态异常");
        }
    }

    /**
     * 查询个人订单数量
     */
    public OrderCountVo queryCount(Long userId) {
        // 待付款数量
        long waitPayCount = this.countByStatus(userId, Collections.singletonList(OrderStatusEnum.WAIT_PAY.getValue()));
        // 待发货数量
        long waitShipCount = this.countByStatus(userId, Collections.singletonList(OrderStatusEnum.WAIT_SHIP.getValue()));
        // 待收货数量
        long waitReceiveCount = this.countByStatus(userId, Collections.singletonList(OrderStatusEnum.WAIT_RECEIVE.getValue()));
        // 待评价数量
        long waitCommentCount = this.countByStatus(userId, OrderStatusEnum.WAIT_REVIEW_STATUS);
        // 退款/售后数量
        long refundAndAfterSaleCount = this.countByStatus(userId, OrderStatusEnum.REFUND_AND_AFTER_SALE_STATUS);

        OrderCountVo vo = new OrderCountVo();
        vo.setWaitPayCount(waitPayCount);
        vo.setWaitShipCount(waitShipCount);
        vo.setWaitReceiveCount(waitReceiveCount);
        vo.setWaitCommentCount(waitCommentCount);
        vo.setRefundAndAfterSaleCount(refundAndAfterSaleCount);
        return vo;
    }

    /**
     * 预览订单
     */
    public List<OrderShopVo> preview(Long userId, OrderPreviewDto dto) {
        Set<Long> skuIds = dto.getItems().stream().map(OrderPreviewItemDto::getSkuId).collect(Collectors.toSet());
        // to map
        Map<Long, Integer> mapSkuQuantity = dto.getItems()
                .stream().collect(Collectors.toMap(OrderPreviewItemDto::getSkuId, OrderPreviewItemDto::getQuantity));

        // 查询sku
        List<ProductSkuEntity> listSku = productSkuRepo.findListByIds(skuIds);
        // 检查
        if (listSku.size() != skuIds.size()) {
            throw new BaseException("订单项中存在无效的SKU ID");
        }
        Set<Long> shopIds = listSku.stream().map(ProductSkuEntity::getShopId).collect(Collectors.toSet());
        this.checkShopScope(shopIds);
        this.checkShopsEnabled(shopIds);

        // --------------------------------------------------------------------------------

        List<OrderShopProductVo> listOrderShopProductVo =
                this.queryOrderShopProductForPreview(listSku, new ArrayList<>(skuIds), mapSkuQuantity);

        // --------------------------------------------------------------------------------

        // group by shop
        Map<Long, List<OrderShopProductVo>> groupOrderShopProductVo =
                listOrderShopProductVo.stream().collect(Collectors.groupingBy(OrderShopProductVo::getShopId));
        List<OrderShopVo> listOrderShopVo = new ArrayList<>();
        for (Map.Entry<Long, List<OrderShopProductVo>> entry : groupOrderShopProductVo.entrySet()) {
            Long shopId = entry.getKey();
            List<OrderShopProductVo> items = entry.getValue();

            OrderShopVo vo = new OrderShopVo();
            vo.setShopId(shopId);
            vo.setShopName(entry.getValue().get(0).getShopName());
            vo.setItems(items);
            listOrderShopVo.add(vo);
        }

        return listOrderShopVo;
    }

    /**
     * 预览订单 for 秒杀场景
     */
    public List<OrderShopVo> previewForSeckill(Long userId, OrderPreviewDto dto) {
        Set<Long> skuIds = dto.getItems().stream().map(OrderPreviewItemDto::getSkuId).collect(Collectors.toSet());
        // to map<seckillSkuId, quantity>
        Map<Long, Integer> mapSkuQuantity = dto.getItems()
                .stream().collect(Collectors.toMap(OrderPreviewItemDto::getSkuId, OrderPreviewItemDto::getQuantity));

        // 查询sku
        List<SeckillSkuEntity> listSku = seckillSkuRepo.findListByIds(skuIds);
        // 检查
        if (listSku.size() != skuIds.size()) {
            throw new BaseException("订单项中存在无效的SKU ID");
        }
        Set<Long> shopIds = listSku.stream().map(SeckillSkuEntity::getShopId).collect(Collectors.toSet());
        this.checkShopScope(shopIds);
        this.checkShopsEnabled(shopIds);

        // --------------------------------------------------------------------------------

        List<OrderShopProductVo> listOrderShopProductVo =
                this.queryOrderShopProductForSeckillPreview(listSku, new ArrayList<>(skuIds), mapSkuQuantity);

        // --------------------------------------------------------------------------------

        // group by shop
        Map<Long, List<OrderShopProductVo>> groupOrderShopProductVo =
                listOrderShopProductVo.stream().collect(Collectors.groupingBy(OrderShopProductVo::getShopId));
        List<OrderShopVo> listOrderShopVo = new ArrayList<>();
        for (Map.Entry<Long, List<OrderShopProductVo>> entry : groupOrderShopProductVo.entrySet()) {
            Long shopId = entry.getKey();
            List<OrderShopProductVo> items = entry.getValue();

            OrderShopVo vo = new OrderShopVo();
            vo.setShopId(shopId);
            vo.setShopName(entry.getValue().get(0).getShopName());
            vo.setItems(items);
            listOrderShopVo.add(vo);
        }

        return listOrderShopVo;
    }

    /**
     * 预览拼团订单
     */
    public List<OrderShopVo> previewForGroupBuy(Long userId, OrderPreviewDto dto) {
        Set<Long> skuIds = dto.getItems().stream().map(OrderPreviewItemDto::getSkuId).collect(Collectors.toSet());
        // 先校验拼团SKU完整性，避免重复项或无效ID进入价格预览。
        if (skuIds.size() != dto.getItems().size()) {
            throw new BaseException("订单项中存在重复的拼团SKU ID");
        }
        List<GroupBuySkuEntity> skus = groupBuySkuRepo.findListByIds(skuIds);
        if (skus.size() != skuIds.size()) {
            throw new BaseException("订单项中存在无效的拼团SKU ID");
        }
        Set<Long> shopIds = skus.stream().map(GroupBuySkuEntity::getShopId).collect(Collectors.toSet());
        this.checkShopScope(shopIds);
        this.checkShopsEnabled(shopIds);
        // 按SKU映射购买数量，再复用拼团商品组装逻辑生成店铺维度预览。
        Map<Long, Integer> quantityMap = dto.getItems().stream()
                .collect(Collectors.toMap(OrderPreviewItemDto::getSkuId, OrderPreviewItemDto::getQuantity));
        List<OrderShopProductVo> products = this.queryOrderShopProductForGroupBuyPreview(skus, quantityMap);
        Map<Long, List<OrderShopProductVo>> productByShop = products.stream()
                .collect(Collectors.groupingBy(OrderShopProductVo::getShopId));
        return productByShop.entrySet().stream().map(entry -> {
            OrderShopVo vo = new OrderShopVo();
            vo.setShopId(entry.getKey());
            vo.setShopName(entry.getValue().get(0).getShopName());
            vo.setItems(entry.getValue());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 处理微信支付回调通知
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePayNotify(String channelCode, String notifyData, String serial,
                                   String signature, String timestamp, String nonce) {
        // 解密数据
        WxPayUtils.Notification notification;
        try {
            notification = wxPayService.parseNotification(
                    channelCode, notifyData, serial, signature, timestamp, nonce);
        } catch (Exception e) {
            log.warn("解析微信支付通知数据失败，异常类型：{}", e.getClass().getSimpleName());
            return false;
        }

        String eventType = notification.getEventType();
        if (!Objects.equals(eventType, WX_PAY_NOTIFY_EVENT_TYPE)) {
            log.error("微信支付通知非支付成功状态，eventType={}", eventType);
            return false;
        }

        // 解析订单信息
        WxPayUtils.OrderInfo orderInfo = notification.parseOrderInfo();
        OrderEntity callbackOrder = orderRepo.findOneByOrderNo(orderInfo.getOutTradeNo());
        if (!this.isExpectedPaymentChannel(callbackOrder, channelCode)) {
            log.error("支付回调通道与订单支付通道不一致，订单ID：{}",
                    callbackOrder == null ? null : callbackOrder.getId());
            return false;
        }
        WxPayOrderStatusEnum tradeStateEnum = WxPayOrderStatusEnum.of(orderInfo.getTradeState());

        boolean handleSuccess = true;
        switch (Objects.requireNonNull(tradeStateEnum)) {
            // 支付成功
            case SUCCESS:
                handleSuccess = orderService.handlePaySuccess(notification);
                if (handleSuccess) {
                    OrderEntity order = orderRepo.findOneByOrderNo(orderInfo.getOutTradeNo());
                    if (order != null) {
                        this.markSeckillPaySuccess(order);
                        if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
                            groupBuyTradeService.handleOrderPaid(order.getId());
                        }
                    }
                }
                break;
            // 转入退款
            case REFUND:
                // todo
                break;
            // 未支付
            case NOTPAY:
                // todo
                break;
            // 已关闭
            case CLOSED:
                handleSuccess = orderService.handleOrderClose(notification);
                if (handleSuccess) {
                    OrderEntity order = orderRepo.findOneByOrderNo(orderInfo.getOutTradeNo());
                    if (order != null && OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
                        groupBuyTradeService.handleOrderClosed(order.getId());
                    }
                }
                break;
            default:
                log.info("其他状态暂时不做处理，返回成功");
                break;
        }

        return handleSuccess;
    }

    /**
     * 处理微信退款回调通知
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRefundNotify(String channelCode, String notifyData, String serial,
                                      String signature, String timestamp, String nonce) {
        // 解密数据
        WxPayUtils.Notification notification;
        try {
            notification = wxPayService.parseNotification(
                    channelCode, notifyData, serial, signature, timestamp, nonce);
        } catch (Exception e) {
            log.warn("解析微信退款通知数据失败，异常类型：{}", e.getClass().getSimpleName());
            return false;
        }

        String eventType = notification.getEventType();
        log.info("微信退款通知，eventType={}", eventType);

        // 解析退款信息
        notification.parseRefundInfo();
        OrderEntity callbackOrder = orderRepo.findOneByOrderNo(notification.getRefundInfo().getOutTradeNo());
        if (!this.isExpectedPaymentChannel(callbackOrder, channelCode)) {
            log.error("退款回调通道与订单支付通道不一致，订单ID：{}",
                    callbackOrder == null ? null : callbackOrder.getId());
            return false;
        }

        // 处理退款业务
        boolean handled = orderService.handleRefundNotify(notification);
        if (handled && notification.getRefundInfo() != null
                && WxRefundStatusEnum.SUCCESS.equalsValue(notification.getRefundInfo().getRefundStatus())) {
            // 仅退款成功才回补拼团或源SKU库存，处理中和失败状态不能重复回补。
            groupBuyTradeService.handleRefundSuccess(notification.getRefundInfo().getOutTradeNo());
        }
        return handled;
    }

    /**
     * 查询订单详情
     */
    public OrderDetailVo queryDetail(Long userId, Long orderId) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.findOneById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        // 检查：订单归属
        if (!Objects.equals(order.getMemberId(), userId)) {
            throw new RuntimeException("无该订单权限");
        }
        this.checkOrderScope(order);

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

        ShopEntity shop = shopRepo.findOneById(order.getShopId());
        OrderVo orderVo = BeanUtil.copyProperties(order, OrderVo.class);
        orderVo.setShopName(shop == null ? "" : shop.getShopName());
        if (StrUtil.isBlank(orderVo.getDeliveryProviderName())
                && StrUtil.isNotBlank(order.getDeliveryProviderCode())) {
            DeliveryProviderEntity provider = deliveryProviderRepo.findOneByShopIdAndCode(
                    order.getShopId(), order.getDeliveryProviderCode());
            if (provider != null) {
                orderVo.setDeliveryProviderName(provider.getName());
            }
        }

        OrderDetailVo result = new OrderDetailVo();
        result.setOrder(orderVo);
        result.setShopProducts(listOrderShopProductVo);
        result.setDeliveryTracking(deliveryTracking);
        result.setRefundImages(orderRefundImageRepo.findListByOrderId(order.getId()).stream()
                .map(OrderRefundImageEntity::getImage)
                .collect(Collectors.toList()));
        this.fillGroupBuyInfo(result, order);
        return result;
    }

    /**
     * 为App订单详情补充拼团进度
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
     * 查询订单物流信息
     */
    public JSONObject queryTracking(Long userId, Long orderId) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.findOneById(orderId);
        if (order == null) {
            throw new BaseException("订单不存在");
        }
        // 检查：订单归属
        if (!Objects.equals(order.getMemberId(), userId)) {
            throw new BaseException("无该订单权限");
        }
        this.checkOrderScope(order);

        // 查询物流信息
        JSONObject result = juheExpService.queryExpress(
                order.getDeliveryProviderCode(), order.getTrackingNo(), null, null);

        // App主动查询成功后同步刷新缓存，避免订单详情继续等待定时任务更新。
        OrderDeliveryTrackingEntity tracking = orderDeliveryTrackingRepo.findOneByOrderNo(order.getOrderNo());
        if (tracking != null) {
            LocalDateTime now = LocalDateTime.now();
            tracking.setLastRespData(result == null ? "" : result.toJSONString());
            tracking.setLastQueryTime(now);
            if (result != null && Objects.equals(result.getString("error_code"), "0")) {
                tracking.setLastSuccessRespData(result.toJSONString());
                tracking.setLastSuccessQueryTime(now);
                JSONObject trackingResult = result.getJSONObject("result");
                if (trackingResult != null) {
                    tracking.setTrackingStatus(trackingResult.getString("status_detail"));
                    if (Objects.equals(trackingResult.getString("status"), "1")) {
                        tracking.setStatusLocked(true);
                    }
                }
            }
            orderDeliveryTrackingRepo.updateById(tracking);
        }
        return result;
    }

    /**
     * 取消订单
     * 买家端触发，需要检查订单权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId, String closeReason) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(orderId);
        // 检查：订单归属
        if (!Objects.equals(order.getMemberId(), userId)) {
            throw new RuntimeException("无该订单权限");
        }
        this.checkOrderScope(order);
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_PAY.getValue())) {
            throw new BaseException("仅待付款订单可以取消");
        }

        // 先关闭微信订单，避免取消动作与支付成功并发时错误关闭本地订单。
        boolean paid = wxPayService.closeOrder(order);
        if (paid) {
            throw new BaseException("订单已支付，无法取消");
        }
        orderService.cancelOrder(order, closeReason);
        if (OrderTypeEnum.GROUP_BUY.equalsValue(order.getOrderType())) {
            groupBuyTradeService.handleOrderClosed(order.getId());
        }
    }

    /**
     * 申请退款
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(Long userId, ApplyRefundDto dto) {
        List<String> images = dto.getImages() == null ? Collections.emptyList() : dto.getImages().stream()
                .map(image -> image == null ? "" : image.trim())
                .collect(Collectors.toList());
        if (images.stream().anyMatch(StrUtil::isBlank)) {
            throw new BaseException("退款凭证图片不能为空");
        }
        if (new HashSet<>(images).size() != images.size()) {
            throw new BaseException("退款凭证图片不能重复");
        }

        // 检查：订单是否存在
        OrderEntity order = orderRepo.findOneById(dto.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        // 检查：订单归属
        if (!Objects.equals(order.getMemberId(), userId)) {
            throw new RuntimeException("无该订单权限");
        }
        this.checkOrderScope(order);

        boolean canApply = Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_SHIP.getValue())
                || Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_RECEIVE.getValue())
                || (Objects.equals(order.getStatus(), OrderStatusEnum.COMPLETED.getValue())
                && !Boolean.TRUE.equals(order.getIsReviewed()));
        boolean canReapply = Objects.equals(order.getStatus(), OrderStatusEnum.REFUND.getValue())
                && Objects.equals(order.getRefundStatus(), RefundStatusEnum.REJECTED.getValue());
        if (!canApply && !canReapply) {
            throw new RuntimeException("仅待发货、待收货、待评价订单可申请退款");
        }

        Set<String> existingImages = orderRefundImageRepo.findListByOrderId(order.getId()).stream()
                .map(OrderRefundImageEntity::getImage)
                .collect(Collectors.toSet());
        List<String> newImages = images.stream()
                .filter(image -> !existingImages.contains(image))
                .collect(Collectors.toList());
        if (!newImages.isEmpty()) {
            // 退款申请成功后保留凭证文件，避免被临时文件清理任务删除。
            storageManager.markUse(newImages.toArray(new String[0]));
        }

        if (canReapply) {
            // 仅允许被拒绝的退款申请重新发起，处理中、成功和失败状态不能重复申请。
            if (!orderRepo.updateRefundStatus(order.getId(), RefundStatusEnum.REJECTED.getValue(),
                    RefundStatusEnum.APPLYING.getValue())) {
                throw new RuntimeException("退款申请状态已变更，请刷新后重试");
            }
        } else {
            boolean updateStatusSuccess = orderRepo.updateStatus(
                    order.getId(), order.getStatus(), OrderStatusEnum.REFUND.getValue());
            if (!updateStatusSuccess) {
                throw new RuntimeException("订单状态更新失败");
            }
            order.setStatus(OrderStatusEnum.REFUND.getValue());
        }

        order.setRefundStatus(RefundStatusEnum.APPLYING.getValue());
        order.setRefundApplyTime(LocalDateTime.now());
        order.setRefundApplyReason(dto.getReason().trim());
        order.setRefundApplyDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
        order.setRefundConfirmTime(null);
        order.setRefundConfirmRemark(null);
        order.setRefundRejectTime(null);
        order.setRefundRejectReason(null);
        order.setRefundSuccessTime(null);
        order.setRefundSummary(null);
        orderRepo.updateById(order);

        // 现有退款模型只保留当前申请；重新申请时同步替换原因、说明和凭证。
        orderRefundImageRepo.removeByOrderId(order.getId());
        if (!images.isEmpty()) {
            List<OrderRefundImageEntity> refundImages = new ArrayList<>(images.size());
            for (int index = 0; index < images.size(); index++) {
                OrderRefundImageEntity image = new OrderRefundImageEntity();
                image.setMchId(order.getMchId());
                image.setShopId(order.getShopId());
                image.setMemberId(userId);
                image.setOrderId(order.getId());
                image.setImage(images.get(index));
                image.setSort(index + 1);
                refundImages.add(image);
            }
            orderRefundImageRepo.saveBatch(refundImages);
        }
    }

    /**
     * 确认收货
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long userId, ConfirmReceiveDto dto, String receiveRemark) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.findOneById(dto.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        // 检查：订单归属
        if (!Objects.equals(order.getMemberId(), userId)) {
            throw new RuntimeException("无该订单权限");
        }
        this.checkOrderScope(order);

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
        // 订单完成是“订单完成奖励”唯一触发点，记账服务按订单ID保证重复签收不重复赠送。
        memberPointsService.grantForOrderComplete(order);
        shopSettlementService.recordOrderCompleted(order);
    }

    // ================================================================================

    /**
     * 订单实体包装为App订单列表视图
     */
    private IPage<OrderListVo> wrapOrderListVo(Page<OrderEntity> pageOrder) {
        List<OrderEntity> orders = pageOrder.getRecords();

        // 查询订单明细
        List<Long> orderIds = orders.stream().map(OrderEntity::getId).collect(Collectors.toList());
        List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderIds(orderIds);

        // 查询sku
        Set<Long> skuIds = orderItems.stream().map(OrderItemEntity::getSkuId).collect(Collectors.toSet());
        List<ProductSkuEntity> listSku = productSkuRepo.findListByIds(skuIds);

        // --------------------------------------------------------------------------------

        List<OrderShopProductVo> listOrderShopProductVo =
                this.queryOrderShopProductForOrderList(listSku, new ArrayList<>(skuIds), orderItems);

        // --------------------------------------------------------------------------------

        // group by orderId, and wrap
        Map<Long, List<OrderShopProductVo>> groupOrderShopProductVo =
                listOrderShopProductVo.stream().collect(Collectors.groupingBy(OrderShopProductVo::getOrderId));
        return pageOrder.convert(order -> {
            List<OrderShopProductVo> items = groupOrderShopProductVo.get(order.getId());

            OrderListVo vo = new OrderListVo();
            vo.setShopId(order.getShopId());
            vo.setShopName(items.get(0).getShopName());
            vo.setItems(items);
            // 订单信息
            vo.setOrderId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderType(order.getOrderType());
            vo.setOrderStatus(order.getStatus());
            vo.setRefundStatus(order.getRefundStatus());
            vo.setAfterSaleStatus(order.getAfterSaleStatus());
            vo.setIsReviewed(order.getIsReviewed());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setDiscountAmount(order.getDiscountAmount());
            vo.setFinalAmount(order.getFinalAmount());
            vo.setCreateTime(order.getCreateTime());
            vo.setPaySuccessTime(order.getPaySuccessTime());
            return vo;
        });
    }

    /**
     * 订单预览场景
     */
    private List<OrderShopProductVo> queryOrderShopProductForPreview(List<ProductSkuEntity> listSku, List<Long> skuIds,
                                                                     Map<Long, Integer> mapSkuQuantity) {
        // 查询店铺信息
        Set<Long> shopIds = listSku.stream().map(ProductSkuEntity::getShopId).collect(Collectors.toSet());
        List<ShopEntity> listShop = shopRepo.findListByIds(shopIds);
        Map<Long, ShopEntity> mapShop = listShop.stream().collect(Collectors.toMap(ShopEntity::getId, shop -> shop));

        // 查询spu信息
        Set<Long> spuIds = listSku.stream().map(ProductSkuEntity::getSpuId).collect(Collectors.toSet());
        List<ProductSpuEntity> listSpu = productSpuRepo.findListByIds(spuIds);
        this.checkProductsEnabled(listSpu);
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

        return listSku.stream().map(sku -> {
            OrderShopProductVo vo = new OrderShopProductVo();
            vo.setId(sku.getId());
            vo.setShopId(sku.getShopId());
            Optional.ofNullable(mapShop.get(sku.getShopId())).ifPresent(shop -> vo.setShopName(shop.getShopName()));
            vo.setSpuId(sku.getSpuId());
            Optional.ofNullable(mapSpu.get(sku.getSpuId())).ifPresent(spu -> {
                vo.setSpuName(spu.getName());
                vo.setSpuMainImage(spu.getMainImage());
            });
            vo.setSkuId(sku.getId());
            vo.setSkuName(sku.getName());
            vo.setPrice(sku.getPrice());
            vo.setQuantity(mapSkuQuantity.get(sku.getId()));

            // specs
            List<ProductSkuSpecEntity> skuSpecs =
                    mapSkuSpecs.getOrDefault(sku.getId(), Collections.emptyList());
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

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 订单预览场景 for 秒杀场景
     */
    private List<OrderShopProductVo> queryOrderShopProductForSeckillPreview(
            List<SeckillSkuEntity> listSku, List<Long> skuIds, Map<Long, Integer> mapSkuQuantity) {
        // 查询店铺信息
        Set<Long> shopIds = listSku.stream().map(SeckillSkuEntity::getShopId).collect(Collectors.toSet());
        List<ShopEntity> listShop = shopRepo.findListByIds(shopIds);
        Map<Long, ShopEntity> mapShop = listShop.stream().collect(Collectors.toMap(ShopEntity::getId, shop -> shop));

        // 查询spu信息
        Set<Long> spuIds = listSku.stream().map(SeckillSkuEntity::getSeckillSpuId).collect(Collectors.toSet());
        List<SeckillSpuEntity> listSpu = seckillSpuRepo.findListByIds(spuIds);
        Map<Long, SeckillSpuEntity> mapSpu = listSpu.stream().collect(Collectors.toMap(SeckillSpuEntity::getId, spu -> spu));

        return listSku.stream().map(sku -> {
            OrderShopProductVo vo = new OrderShopProductVo();
            vo.setId(sku.getId());
            vo.setShopId(sku.getShopId());
            Optional.ofNullable(mapShop.get(sku.getShopId())).ifPresent(shop -> vo.setShopName(shop.getShopName()));
            vo.setSpuId(sku.getSpuId());
            Optional.ofNullable(mapSpu.get(sku.getSeckillSpuId())).ifPresent(spu -> {
                vo.setSpuName(spu.getName());
                vo.setSpuMainImage(spu.getMainImage());
            });
            vo.setSkuId(sku.getId());
            vo.setSkuName(sku.getName());
            vo.setPrice(sku.getSeckillPrice());
            vo.setQuantity(mapSkuQuantity.get(sku.getId()));

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 拼团订单预览商品项
     */
    private List<OrderShopProductVo> queryOrderShopProductForGroupBuyPreview(
            List<GroupBuySkuEntity> groupBuySkus, Map<Long, Integer> quantityMap) {
        Set<Long> shopIds = groupBuySkus.stream().map(GroupBuySkuEntity::getShopId).collect(Collectors.toSet());
        Map<Long, ShopEntity> shopMap = shopRepo.findListByIds(shopIds).stream()
                .collect(Collectors.toMap(ShopEntity::getId, shop -> shop));
        Set<Long> spuIds = groupBuySkus.stream().map(GroupBuySkuEntity::getGroupBuySpuId).collect(Collectors.toSet());
        Map<Long, GroupBuySpuEntity> spuMap = groupBuySpuRepo.findListByIds(spuIds).stream()
                .collect(Collectors.toMap(GroupBuySpuEntity::getId, spu -> spu));

        return groupBuySkus.stream().map(sku -> {
            OrderShopProductVo vo = new OrderShopProductVo();
            vo.setId(sku.getId());
            vo.setShopId(sku.getShopId());
            Optional.ofNullable(shopMap.get(sku.getShopId())).ifPresent(shop -> vo.setShopName(shop.getShopName()));
            GroupBuySpuEntity spu = spuMap.get(sku.getGroupBuySpuId());
            vo.setSpuId(sku.getSpuId());
            if (spu != null) {
                vo.setSpuName(spu.getName());
                vo.setSpuMainImage(groupBuyImageService.resolveSpuMainImage(spu));
            }
            vo.setSkuId(sku.getId());
            vo.setSkuName(sku.getName());
            vo.setPrice(sku.getGroupPrice());
            vo.setQuantity(quantityMap.get(sku.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 订单列表场景
     */
    private List<OrderShopProductVo> queryOrderShopProductForOrderList(List<ProductSkuEntity> listSku,
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
            vo.setId(orderItem.getId());
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
     * 设置订单收货地址
     */
    private void setOrderReceiverAddress(OrderEntity order, MemberAddressEntity address) {
        order.setReceiverName(address.getName());
        order.setReceiverContact(address.getContact());
        order.setReceiverProvinceCode(address.getProvinceCode());
        order.setReceiverProvinceName(address.getProvinceName());
        order.setReceiverCityCode(address.getCityCode());
        order.setReceiverCityName(address.getCityName());
        order.setReceiverDistrictCode(address.getDistrictCode());
        order.setReceiverDistrictName(address.getDistrictName());
        order.setReceiverDetailAddress(address.getDetailAddress());
        order.setReceiverFullAddress(address.getFullAddress());
        order.setReceiverPostalCode(address.getPostalCode());
    }

    /**
     * 计算订单支付超时时间
     */
    private LocalDateTime calcOrderPayExpireTime() {
        return LocalDateTime.now().plusMinutes(orderPayTimeoutMinutes);
    }

    /**
     * 计算订单支付超时时间 for 秒杀场景
     */
    private LocalDateTime calcOrderPayExpireTimeForSeckill() {
        return LocalDateTime.now().plusMinutes(seckillProperties.getPayTimeoutMinutes());
    }

    /**
     * 创建新团或校验可参与的团
     */
    private GroupBuyGroupEntity prepareGroup(Long userId, Long groupId, boolean randomJoin,
                                             GroupBuyActivityEntity activity, GroupBuySpuEntity groupBuySpu) {
        // 同一会员在同一活动只能保留一个有效拼团，不能跨团重复参与。
        long participated = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getActivityId, activity.getId())
                .eq(GroupBuyOrderEntity::getMemberId, userId)
                .in(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.WAIT_PAY.getValue(),
                        GroupBuyOrderStatusEnum.PAID.getValue(), GroupBuyOrderStatusEnum.REFUNDING.getValue(),
                        GroupBuyOrderStatusEnum.REFUNDED.getValue(), GroupBuyOrderStatusEnum.REFUND_FAILED.getValue())
                .count();
        if (participated > 0) {
            throw new BaseException("您已参与当前拼团活动，不能再参与其他团");
        }
        if (activity.getGroupLimit() == null || activity.getGroupLimit() == 0) {
            throw new BaseException("当前拼团活动尚未设置最大开团数");
        }
        if (randomJoin && groupId != null) {
            throw new BaseException("随机加入和指定拼团不能同时提交");
        }

        if (randomJoin) {
            // 随机参团优先选择已支付人数最多、创建最早且仍有席位的团。
            return this.prepareRandomGroup(activity, groupBuySpu);
        }
        if (groupId == null) {
            // 锁定活动行并统计已创建的团，避免并发开团突破活动上限。
            GroupBuyActivityEntity lockedActivity = groupBuyActivityRepo.findOneByIdForUpdate(activity.getId());
            if (lockedActivity == null) {
                throw new BaseException("拼团活动不存在");
            }
            Integer groupLimit = lockedActivity.getGroupLimit();
            if (groupLimit == null || groupLimit == 0) {
                throw new BaseException("当前拼团活动尚未设置最大开团数");
            }
            if (groupLimit > 0) {
                long groupCount = groupBuyGroupRepo.lambdaQuery()
                        .eq(GroupBuyGroupEntity::getActivityId, lockedActivity.getId())
                        // 团长未支付而关闭的团属于临时占位，不消耗活动的最大团数。
                        .ne(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.CLOSED.getValue())
                        .count();
                if (groupCount >= groupLimit) {
                    throw new BaseException("当前拼团活动已达到最大开团数");
                }
            }

            // 新团由首位下单用户担任团长，先占一个待支付席位但尚未计入已支付人数。
            GroupBuyGroupEntity group = new GroupBuyGroupEntity();
            group.setMchId(groupBuySpu.getMchId());
            group.setShopId(groupBuySpu.getShopId());
            group.setGroupNo(idService.nextEncodedId());
            group.setActivityId(lockedActivity.getId());
            group.setGroupBuySpuId(groupBuySpu.getId());
            group.setLeaderMemberId(userId);
            group.setMemberCount(0);
            group.setReservedMemberCount(1);
            group.setMinPeople(lockedActivity.getMinPeople());
            group.setStatus(GroupBuyGroupStatusEnum.WAIT_PAY.getValue());
            group.insert();
            return group;
        }

        GroupBuyGroupEntity group = this.reserveGroupSeat(groupId, activity, groupBuySpu);
        if (group == null) {
            throw new BaseException("该拼团已满或不可参与");
        }
        return group;
    }

    /**
     * 随机选择一个即将成团且仍有席位的团。
     */
    private GroupBuyGroupEntity prepareRandomGroup(GroupBuyActivityEntity activity, GroupBuySpuEntity groupBuySpu) {
        List<Long> groupIds = groupBuyGroupRepo.lambdaQuery()
                .eq(GroupBuyGroupEntity::getActivityId, activity.getId())
                .eq(GroupBuyGroupEntity::getGroupBuySpuId, groupBuySpu.getId())
                .eq(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.FORMING.getValue())
                .orderByDesc(GroupBuyGroupEntity::getMemberCount)
                .orderByAsc(GroupBuyGroupEntity::getCreateTime)
                .list().stream()
                .map(GroupBuyGroupEntity::getId)
                .collect(Collectors.toList());
        for (Long groupId : groupIds) {
            // 每次尝试都会锁定具体团，其他用户可能已抢先占满，失败后继续找下一团。
            GroupBuyGroupEntity group = this.reserveGroupSeat(groupId, activity, groupBuySpu);
            if (group != null) {
                return group;
            }
        }
        throw new BaseException("当前暂无可加入的拼团");
    }

    /**
     * 锁定指定拼团并占用一个席位。
     */
    private GroupBuyGroupEntity reserveGroupSeat(Long groupId, GroupBuyActivityEntity activity,
                                                  GroupBuySpuEntity groupBuySpu) {
        GroupBuyGroupEntity group = groupBuyGroupRepo.findOneByIdForUpdate(groupId);
        if (group == null) {
            throw new BaseException("拼团记录不存在");
        }
        if (!GroupBuyGroupStatusEnum.FORMING.equalsValue(group.getStatus())
                || !Objects.equals(group.getActivityId(), activity.getId())
                || !Objects.equals(group.getGroupBuySpuId(), groupBuySpu.getId())) {
            throw new BaseException("该拼团已结束或不可参与");
        }
        int memberCount = group.getMemberCount() == null ? 0 : group.getMemberCount();
        int reservedMemberCount = Math.max(group.getReservedMemberCount() == null ? 0 : group.getReservedMemberCount(),
                memberCount);
        if (reservedMemberCount >= group.getMinPeople()) {
            return null;
        }
        boolean reserved = groupBuyGroupRepo.reserveSeatIfAvailable(
                group.getId(), GroupBuyGroupStatusEnum.FORMING.getValue());
        if (!reserved) {
            return null;
        }
        group.setReservedMemberCount(reservedMemberCount + 1);
        return group;
    }

    /**
     * 尝试秒杀占用库存
     *
     * @return -1: 库存不足 -2: 已经秒杀过了 >=0: 成功，返回剩余库存
     */
    private long trySeckill(Long userId, Long skuId, Long seckillSpuId) {
        String lua = BizConsts.SECKILL_STOCK_SCRIPT;

        // keys
        List<String> keys = Arrays.asList(String.format(BizConsts.SECKILL_STOCK_KEY, skuId),
                String.format(BizConsts.SECKILL_QUOTA_KEY, skuId),
                String.format(BizConsts.SECKILL_SPU_QUOTA_KEY, seckillSpuId));
        // args
        List<String> args = Collections.singletonList(userId.toString());

        return cacheService.executeLua(lua, keys, args);
    }

    /**
     * 标记秒杀订单已经支付，避免超时任务继续发起取消。
     */
    private void markSeckillPaySuccess(OrderEntity order) {
        if (order == null || !OrderTypeEnum.SECKILL.equalsValue(order.getOrderType())) {
            return;
        }
        List<OrderItemEntity> orderItems = orderItemRepo.findListByOrderId(order.getId());
        if (orderItems.isEmpty()) {
            log.warn("秒杀订单没有订单明细，无法更新支付事件，orderId={}", order.getId());
            return;
        }
        Long skuId = orderItems.get(0).getSkuId();
        String eventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, order.getMemberId());
        SeckillEventCacheVo event = cacheService.get(eventKey, SeckillEventCacheVo.class);
        if (event == null || !Objects.equals(event.getOrderId(), order.getId())) {
            return;
        }
        event.setPayTime(order.getPaySuccessTime() == null ? LocalDateTime.now() : order.getPaySuccessTime());
        cacheService.set(eventKey, JSON.toJSONString(event));
    }

    /**
     * 为订单查询追加当前独立店铺范围
     */
    private void applyShopScope(LambdaQueryWrapper<OrderEntity> wrapper) {
        if (mallAppContextService.isShopMode()) {
            wrapper.eq(OrderEntity::getShopId, mallAppContextService.getShopId());
        }
    }

    /**
     * 统计当前商城范围内的订单数量
     */
    private long countByStatus(Long userId, List<String> statuses) {
        LambdaQueryWrapper<OrderEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderEntity::getMemberId, userId)
                .in(OrderEntity::getStatus, statuses);
        this.applyShopScope(wrapper);
        return orderRepo.count(wrapper);
    }

    /**
     * 校验订单是否属于当前独立店铺
     */
    private void checkOrderScope(OrderEntity order) {
        mallAppContextService.checkShopScope(order.getShopId());
    }

    /**
     * 校验订单商品是否属于当前独立店铺
     */
    private void checkShopScope(Collection<Long> shopIds) {
        if (!mallAppContextService.isShopMode()) {
            return;
        }
        if (shopIds.size() != 1 || !shopIds.contains(mallAppContextService.getShopId())) {
            throw new BaseException("当前商城不可购买其他店铺商品");
        }
    }

    /**
     * 检查订单涉及的店铺是否仍在营业
     */
    private void checkShopsEnabled(Collection<Long> shopIds) {
        List<ShopEntity> shops = shopRepo.findListByIds(shopIds);
        if (shops.size() != shopIds.size()
                || shops.stream().anyMatch(shop -> !Boolean.TRUE.equals(shop.getIsEnabled())
                        || !Boolean.TRUE.equals(shop.getIsOpen()))) {
            throw new BaseException("订单中存在已停止营业的店铺");
        }
    }

    /**
     * 保存支付和结算模式快照
     */
    private void initializePaymentSnapshot(OrderEntity order, String appTarget) {
        // 测试阶段网关实际扣款固定为1分，后续切换生产金额时只调整此入口。
        order.setPayAmount(BigDecimal.valueOf(WxPayService.TEST_PAYMENT_AMOUNT_FEN, 2));
        order.setAppTarget(StrUtil.blankToDefault(appTarget, "platform"));
        ShopEntity shop = shopRepo.getOneById(order.getShopId(), "店铺不存在");
        if (!Boolean.TRUE.equals(shop.getIsEnabled()) || !Boolean.TRUE.equals(shop.getIsOpen())) {
            throw new BaseException("店铺已停止营业，暂时无法下单");
        }
        // 下单时解析并固化支付路由，后续查单、退款不受通道配置变化影响。
        ResolvedPaymentChannel channel = paymentChannelService.resolveForNewOrder(
                order.getShopId(), order.getAppTarget());
        order.setPaymentChannelId(channel.getRouteId());
        order.setPaymentChannelCode(channel.getChannelCode());
        order.setPaymentAppId(channel.getAppId());
        order.setPaymentMchId(channel.getMchId());
        order.setPaymentSubMchId(channel.getSubMchId());
        order.setCollectionMode(channel.getCollectionMode());
        // 平台普通商户代收需要线下结算，服务商和店铺直连资金直接进入对应商户。
        order.setSettlementMode(PaymentCollectionModeEnum.PLATFORM_DIRECT.getValue()
                .equals(channel.getCollectionMode())
                ? OrderSettlementModeEnum.MANUAL.getValue() : OrderSettlementModeEnum.NONE.getValue());
        // 佣金和账期保存订单快照，店铺后续改配置不影响历史结算。
        order.setSettlementCommissionRate(shop.getSettlementCommissionRate() == null
                ? BigDecimal.ZERO : shop.getSettlementCommissionRate());
        order.setSettlementDelayDays(shop.getSettlementDelayDays() == null
                ? 0 : Math.max(shop.getSettlementDelayDays(), 0));
    }

    /**
     * 校验回调使用的通道是否与订单快照一致
     */
    private boolean isExpectedPaymentChannel(OrderEntity order, String channelCode) {
        if (order == null) {
            return true;
        }
        String expected = StrUtil.blankToDefault(
                order.getPaymentChannelCode(), ResolvedPaymentChannel.LEGACY_CHANNEL_CODE);
        String actual = StrUtil.blankToDefault(channelCode, ResolvedPaymentChannel.LEGACY_CHANNEL_CODE);
        return Objects.equals(expected, actual);
    }

    /**
     * 检查优惠券当前情况是否可用
     */
    private CouponApplyResult checkCouponAvailable(Long couponId, Long userId, Long shopId,
                                                   Map<Long, BigDecimal> mapSpuAmount,
                                                   Map<Long, BigDecimal> mapCategoryAmount,
                                                   BigDecimal totalAmount, boolean groupBuy) {
        if (couponId == null) {
            return null;
        }

        MemberCouponEntity coupon = memberCouponRepo.getOneById(couponId, "用户优惠券不存在");
        if (!Objects.equals(coupon.getMemberId(), userId)) {
            throw new BaseException("优惠券不属于当前用户");
        }
        if (Boolean.TRUE.equals(coupon.getIsUsed())) {
            throw new BaseException("优惠券已被使用");
        }
        if (groupBuy && !Boolean.TRUE.equals(coupon.getIsSupportGroupBuy())) {
            // 拼团券需要单独授权，普通订单可用不代表拼团订单可用。
            throw new BaseException("该优惠券不支持拼团订单");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getBeginTime() == null || coupon.getEndTime() == null) {
            throw new BaseException("优惠券有效期异常");
        }
        if (now.isBefore(coupon.getBeginTime()) || now.isAfter(coupon.getEndTime())) {
            throw new BaseException("优惠券已过期");
        }

        CouponTypeEnum couponType = CouponTypeEnum.of(coupon.getType());
        if (couponType == null) {
            throw new BaseException("优惠券类型错误");
        }
        CouponUseRangeEnum useRange = CouponUseRangeEnum.of(coupon.getUseRange());
        if (useRange == null) {
            throw new BaseException("优惠券使用范围错误");
        }

        BigDecimal scopeAmount;
        if (useRange == CouponUseRangeEnum.SHOP) {
            if (!Objects.equals(coupon.getTargetId(), shopId)) {
                throw new BaseException("优惠券不适用于当前店铺");
            }
            scopeAmount = totalAmount;
        } else if (useRange == CouponUseRangeEnum.PRODUCT) {
            // 指定商品券只以命中SPU的金额计算门槛和折扣。
            scopeAmount = mapSpuAmount.get(coupon.getTargetId());
        } else if (useRange == CouponUseRangeEnum.CATEGORY) {
            // 指定类目券将同类目订单项金额合并计算。
            scopeAmount = mapCategoryAmount.get(coupon.getTargetId());
        } else {
            throw new BaseException("优惠券使用范围错误");
        }

        if (scopeAmount == null) {
            throw new BaseException("优惠券不适用于当前商品");
        }

        BigDecimal minAmount = coupon.getMinAmount() == null ? BigDecimal.ZERO : coupon.getMinAmount();
        if (scopeAmount.compareTo(minAmount) < 0) {
            throw new BaseException("优惠券未达到使用门槛");
        }

        BigDecimal discountAmount;
        if (couponType == CouponTypeEnum.FULL_REDUCTION || couponType == CouponTypeEnum.NO_THRESHOLD) {
            if (coupon.getAmount() == null) {
                throw new BaseException("优惠券金额异常");
            }
            discountAmount = coupon.getAmount().min(scopeAmount);
        } else if (couponType == CouponTypeEnum.DISCOUNT) {
            if (coupon.getDiscount() == null) {
                throw new BaseException("优惠券折扣异常");
            }
            discountAmount = scopeAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscount()))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new BaseException("优惠券类型错误");
        }

        CouponApplyResult result = new CouponApplyResult();
        result.memberCoupon = coupon;
        result.discountAmount = discountAmount;
        return result;
    }

    /**
     * 校验普通商品仍处于上架状态
     */
    private void checkProductsEnabled(Collection<ProductSpuEntity> products) {
        if (products.stream().anyMatch(product -> !Boolean.TRUE.equals(product.getIsEnabled()))) {
            throw new BaseException("订单中存在已下架商品");
        }
    }

    /**
     * 将二级分类商品金额同步累加到一级分类
     */
    private void mergeParentCategoryAmount(Map<Long, BigDecimal> categoryAmount) {
        if (categoryAmount.isEmpty()) {
            return;
        }
        List<ProductCategoryEntity> categories = productCategoryRepo.findListByIds(categoryAmount.keySet());
        for (ProductCategoryEntity category : categories) {
            if (category.getParentId() != null && category.getParentId() > 0) {
                categoryAmount.merge(category.getParentId(), categoryAmount.get(category.getId()), BigDecimal::add);
            }
        }
    }

    /**
     * 订单优惠券试算结果
     */
    private static class CouponApplyResult {

        /**
         * 实际使用的会员优惠券
         */
        private MemberCouponEntity memberCoupon;

        /**
         * 本次优惠金额
         */
        private BigDecimal discountAmount;
    }
}
