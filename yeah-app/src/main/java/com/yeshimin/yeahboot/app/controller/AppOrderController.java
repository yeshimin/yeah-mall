package com.yeshimin.yeahboot.app.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.api.encryption.ApiEncryption;
import com.yeshimin.yeahboot.api.encryption.ApiEncryptionPolicy;
import com.yeshimin.yeahboot.app.common.enums.OrderSceneEnum;
import com.yeshimin.yeahboot.app.domain.dto.*;
import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.*;
import com.yeshimin.yeahboot.app.service.AppOrderService;
import com.yeshimin.yeahboot.app.service.MallAppContextService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillActivityCacheVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSkuCacheVo;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqPublisher;
import com.yeshimin.yeahboot.service.WxPayInfoVo;
import com.yeshimin.yeahboot.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;

/**
 * 订单表
 */
@Slf4j
@RestController
@RequestMapping("/app/order")
@RequiredArgsConstructor
public class AppOrderController extends BaseController {

    private final AppOrderService appOrderService;

    private final WxPayService wxPayService;
    private final CacheService cacheService;
    private final MqPublisher mqPublisher;
    private final MallAppContextService mallAppContextService;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public R<OrderSubmitVo> submit(@Validated @RequestBody OrderSubmitDto dto) {
        Long userId = super.getUserId();
        OrderEntity order = appOrderService.submit(userId, dto);

        // 生成支付信息
        WxPayInfoVo payInfoVo = appOrderService.genPayInfo(userId, order.getId());

        OrderSubmitVo vo = BeanUtil.copyProperties(payInfoVo, OrderSubmitVo.class);
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        return R.ok(vo);
    }

    /**
     * 提交拼团订单
     */
    @PostMapping("/submitForGroupBuy")
    public R<OrderSubmitVo> submitForGroupBuy(@Validated @RequestBody GroupBuyOrderSubmitDto dto) {
        Long userId = super.getUserId();
        OrderEntity order = appOrderService.submitForGroupBuy(userId, dto);
        WxPayInfoVo payInfoVo = appOrderService.genPayInfo(userId, order.getId());

        OrderSubmitVo vo = BeanUtil.copyProperties(payInfoVo, OrderSubmitVo.class);
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        return R.ok(vo);
    }

    /**
     * 提交订单 for 秒杀场景
     */
    @PostMapping("/submitForSeckill")
    public R<Void> submitForSeckill(@Validated @RequestBody OrderSubmitDto dto) {
        // 检查参数：场景固定为（秒杀场景）
        if (!Objects.equals(dto.getScene(), String.valueOf(OrderSceneEnum.SECKILL.getValue()))) {
            throw new BaseException(ErrorCodeEnum.FAIL, "场景值参数错误");
        }
        // 检查参数：数量固定为1，且只能有一个商品项
        if (dto.getItems().size() != 1 || dto.getItems().get(0).getQuantity() != 1) {
            throw new BaseException(ErrorCodeEnum.FAIL, "参数错误");
        }
        // 检查参数：当前用户是否已经抢占到该sku的购买资格
        Long userId = super.getUserId();
        Long skuId = dto.getItems().get(0).getSkuId();
        Long contextShopId = mallAppContextService.isShopMode() ? mallAppContextService.getShopId() : null;
        appOrderService.checkSeckillShopScope(skuId);
        SeckillSkuCacheVo skuCacheVo = this.getSeckillSkuCache(skuId);
        String userIdText = String.valueOf(userId);
        boolean skuQuota = cacheService.isMember(
                String.format(BizConsts.SECKILL_QUOTA_KEY, skuId), userIdText);
        String spuQuotaKey = String.format(BizConsts.SECKILL_SPU_QUOTA_KEY, skuCacheVo.getSeckillSpuId());
        boolean spuQuota = cacheService.isMember(spuQuotaKey, userIdText);
        if (skuQuota && !spuQuota) {
            // 兼容升级前已抢到的SKU资格，提交订单时补齐商品级限购记录。
            cacheService.addMember(spuQuotaKey, userIdText);
            spuQuota = true;
        }
        boolean isOk = skuQuota && spuQuota;
        if (!isOk) {
            throw new BaseException(ErrorCodeEnum.FAIL, "您没有抢占到该商品");
        }

        String seckillEventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, userId);
        SeckillEventCacheVo seckillEvent = cacheService.get(seckillEventKey, SeckillEventCacheVo.class);
        if (seckillEvent == null) {
            throw new BaseException(ErrorCodeEnum.FAIL, "秒杀信息未找到");
        }
        String attemptId = seckillEvent.getAttemptId();
        if (attemptId == null || attemptId.trim().isEmpty()) {
            // 兼容升级前已经抢到名额的缓存数据。
            attemptId = userIdText;
            seckillEvent.setAttemptId(attemptId);
        }

        // 同一次抢购尝试只能提交一次；取消后新attempt仍可再次下单。
        String orderKey = String.format(BizConsts.SECKILL_ORDER_KEY, skuId);
        long count = cacheService.addMember(orderKey, attemptId);
        if (count == 0) {
            throw new BaseException(ErrorCodeEnum.FAIL, "不能重复下单");
        }

        // 添加下单记录期间attempt可能已经超时，写回前必须再次确认仍是当前尝试。
        SeckillEventCacheVo currentEvent = cacheService.get(seckillEventKey, SeckillEventCacheVo.class);
        String currentAttemptId = currentEvent == null ? null : currentEvent.getAttemptId();
        currentAttemptId = currentAttemptId == null || currentAttemptId.trim().isEmpty()
                ? userIdText : currentAttemptId;
        if (currentEvent == null || !attemptId.equals(currentAttemptId)) {
            cacheService.removeMember(orderKey, attemptId);
            throw new BaseException(ErrorCodeEnum.FAIL, "本次抢购已超时，请重新参与");
        }
        currentEvent.setAttemptId(attemptId);
        currentEvent.setOrderTime(LocalDateTime.now());
        cacheService.set(seckillEventKey, JSONObject.toJSONString(currentEvent));

        // 发布到秒杀队列异步处理
        MqMessage message = new MqMessage();
        message.setTopic(BizConsts.SECKILL_ORDER_TOPIC);
        message.setPayload(SeckillMqPayload.of(
                dto, userId, contextShopId, mallAppContextService.getAppTarget(), attemptId).toJsonString());
        mqPublisher.publish(message);

        return R.ok();
    }

    /**
     * 秒杀
     */
    @PostMapping("/seckill")
    public R<OrderSeckillVo> seckill(@Validated @RequestBody SeckillDto dto) {
        // 检查参数：skuId和activityId对应关系是否正确
        boolean isMember = cacheService.isMember(String.format(
                BizConsts.SECKILL_ACTIVITY_SKUS_KEY, dto.getActivityId()), String.valueOf(dto.getSkuId()));
        if (!isMember) {
            throw new BaseException("活动信息错误");
        }
        // 判断秒杀是否已经开始
        SeckillActivityCacheVo activityCacheVo = appOrderService.getSeckillActivityCache(dto.getActivityId());
        if (activityCacheVo == null) {
            throw new BaseException("活动配置未找到");
        }
        if (!Boolean.TRUE.equals(activityCacheVo.getIsEnabled())
                || !SeckillActivityStatusEnum.ACTIVITY_STARTED.equalsValue(activityCacheVo.getStatus())) {
            throw new BaseException("活动已结束或暂停");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activityCacheVo.getActivityBeginTime()) || now.isAfter(activityCacheVo.getActivityEndTime())) {
            throw new BaseException("不在活动期间");
        }
        SeckillSkuCacheVo skuCacheVo = this.getSeckillSkuCache(dto.getSkuId());
        if (!Objects.equals(skuCacheVo.getActivityId(), dto.getActivityId())) {
            throw new BaseException("活动信息错误");
        }
        if (now.isBefore(skuCacheVo.getSessionBeginTime()) || now.isAfter(skuCacheVo.getSessionEndTime())) {
            throw new BaseException("不在当前秒杀场次期间");
        }

        Long userId = super.getUserId();
        OrderSeckillVo result = appOrderService.seckill(userId, dto);
        return R.ok(result);
    }

    /**
     * 查询秒杀SKU活动与场次缓存
     */
    private SeckillSkuCacheVo getSeckillSkuCache(Long skuId) {
        SeckillSkuCacheVo result = appOrderService.getSeckillSkuCache(skuId);
        if (result == null || result.getSeckillSpuId() == null
                || result.getSessionBeginTime() == null || result.getSessionEndTime() == null) {
            throw new BaseException("秒杀商品配置未找到");
        }
        return result;
    }

    /**
     * 查询秒杀结果
     */
    @GetMapping("/querySeckillResult")
    public R<SeckillBizResultVo> querySeckillResult(@RequestParam("skuId") Long skuId) {
        Long userId = super.getUserId();
        SeckillBizResultVo result = appOrderService.querySeckillResult(userId, skuId);
        return R.ok(result);
    }

    /**
     * 生成支付信息
     */
    @PostMapping("/genPayInfo")
    public R<WxPayInfoVo> genPayInfo(@Validated @RequestBody OrderNoDto dto) {
        Long userId = super.getUserId();
        WxPayInfoVo payInfoVo = appOrderService.genPayInfo(userId, dto.getOrderId());
        return R.ok(payInfoVo);
    }

    /**
     * 查询订单支付结果
     */
    @GetMapping("/queryPayResult")
    public R<OrderPayResultVo> queryPayResult(@RequestParam("orderId") Long orderId) {
        Long userId = super.getUserId();
        OrderPayResultVo payResultVo = appOrderService.queryPayResult(userId, orderId);
        return R.ok(payResultVo);
    }

    /**
     * 按订单号查询支付结果，兼容旧版App支付结果页。
     */
    @GetMapping("/queryPayOrderInfo")
    public R<OrderPayResultVo> queryPayOrderInfo(@RequestParam("orderNo") String orderNo) {
        Long userId = super.getUserId();
        return R.ok(appOrderService.queryPayResultByOrderNo(userId, orderNo));
    }

    /**
     * 微信支付通知
     * https://pay.weixin.qq.com/doc/v3/merchant/4012791861
     */
    @PublicAccess
    @ApiEncryption(ApiEncryptionPolicy.BYPASS)
    @PostMapping("/wxpay/payNotify")
    public ResponseEntity<Void> wxPayNotify(
            @RequestParam(value = "channelCode", required = false) String channelCode,
            HttpServletRequest request,
            @RequestHeader(value = "Wechatpay-Serial", required = false) String serial,
            @RequestHeader(value = "Wechatpay-Signature", required = false) String signature,
            @RequestHeader(value = "Wechatpay-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "Wechatpay-Nonce", required = false) String nonce) {
        String notifyData = this.readRawBody(request);

        // 验证签名
        boolean verifySuccess = wxPayService.verifySign(
                channelCode, notifyData, serial, signature, timestamp, nonce);
        if (!verifySuccess) {
            log.warn("微信支付通知验签失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 处理业务
        boolean handleSuccess = false;
        try {
            handleSuccess = appOrderService.handlePayNotify(
                    channelCode, notifyData, serial, signature, timestamp, nonce);
        } catch (Exception e) {
            log.warn("微信支付通知处理异常，异常类型：{}", e.getClass().getSimpleName());
        }
        if (!handleSuccess) {
            log.warn("微信支付通知处理失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 微信退款通知
     * https://pay.weixin.qq.com/doc/v3/merchant/4012791906
     */
    @PublicAccess
    @ApiEncryption(ApiEncryptionPolicy.BYPASS)
    @PostMapping("/wxpay/refundNotify")
    public ResponseEntity<Void> wxRefundNotify(
            @RequestParam(value = "channelCode", required = false) String channelCode,
            HttpServletRequest request,
            @RequestHeader(value = "Wechatpay-Serial", required = false) String serial,
            @RequestHeader(value = "Wechatpay-Signature", required = false) String signature,
            @RequestHeader(value = "Wechatpay-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "Wechatpay-Nonce", required = false) String nonce) {
        String notifyData = this.readRawBody(request);

        // 验证签名
        boolean verifySuccess = wxPayService.verifySign(
                channelCode, notifyData, serial, signature, timestamp, nonce);
        if (!verifySuccess) {
            log.warn("微信退款通知验签失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 处理业务
        boolean handleSuccess = false;
        try {
            handleSuccess = appOrderService.handleRefundNotify(
                    channelCode, notifyData, serial, signature, timestamp, nonce);
        } catch (Exception e) {
            log.warn("微信退款通知处理异常，异常类型：{}", e.getClass().getSimpleName());
        }
        if (!handleSuccess) {
            log.warn("微信退款通知处理失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 读取微信通知原始请求体，避免重新序列化导致验签失败。
     */
    private String readRawBody(HttpServletRequest request) {
        try {
            return new String(IoUtil.readBytes(request.getInputStream()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BaseException("读取微信支付通知失败");
        }
    }

    /**
     * 查询个人订单
     */
    @GetMapping("/query")
    public R<IPage<OrderListVo>> query(Page<OrderEntity> page, @Validated OrderQueryDto dto) {
        Long userId = super.getUserId();
        return R.ok(appOrderService.query(userId, page, dto));
    }

    /**
     * 查询个人订单数量
     */
    @GetMapping("/count")
    public R<OrderCountVo> queryCount() {
        Long userId = super.getUserId();
        return R.ok(appOrderService.queryCount(userId));
    }

    /**
     * 预览订单
     * 暂时仅支持一个店铺下一个或多个商品订单
     */
    @PostMapping("/preview")
    public R<List<OrderShopVo>> preview(@Validated @RequestBody OrderPreviewDto dto) {
        Long userId = super.getUserId();
        return R.ok(appOrderService.preview(userId, dto));
    }

    /**
     * 预览订单 for 秒杀场景
     * 暂时仅支持一个店铺下一个或多个商品订单
     */
    @PostMapping("/previewForSeckill")
    public R<List<OrderShopVo>> previewForSeckill(@Validated @RequestBody OrderPreviewDto dto) {
        // 检查参数：数量固定为1，且只能有一个商品项
        if (dto.getItems().size() != 1 || dto.getItems().get(0).getQuantity() != 1) {
            throw new BaseException(ErrorCodeEnum.FAIL, "参数错误");
        }
        // 检查参数：当前用户是否已经抢占到该sku的购买资格
        Long userId = super.getUserId();
        Long skuId = dto.getItems().get(0).getSkuId();
        boolean isOk = cacheService.isMember(String.format(BizConsts.SECKILL_QUOTA_KEY, skuId), String.valueOf(userId));
        if (!isOk) {
            throw new BaseException(ErrorCodeEnum.FAIL, "您没有抢占到该商品");
        }

        return R.ok(appOrderService.previewForSeckill(userId, dto));
    }

    /**
     * 预览拼团订单
     */
    @PostMapping("/previewForGroupBuy")
    public R<List<OrderShopVo>> previewForGroupBuy(@Validated @RequestBody OrderPreviewDto dto) {
        if (dto.getItems().size() != 1 || dto.getItems().get(0).getQuantity() != 1) {
            throw new BaseException(ErrorCodeEnum.FAIL, "参数错误");
        }
        return R.ok(appOrderService.previewForGroupBuy(super.getUserId(), dto));
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/detail")
    public R<OrderDetailVo> queryDetail(@RequestParam("orderId") Long orderId) {
        Long userId = super.getUserId();
        return R.ok(appOrderService.queryDetail(userId, orderId));
    }

    /**
     * 查询订单物流信息
     */
    @GetMapping("/queryTracking")
    public R<JSONObject> queryTracking(@RequestParam("orderId") Long orderId) {
        Long userId = super.getUserId();
        JSONObject result = appOrderService.queryTracking(userId, orderId);
        return R.ok(result);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    public R<Void> cancel(@Validated @RequestBody IdDto dto) {
        Long userId = super.getUserId();
        appOrderService.cancelOrder(userId, dto.getId(), "买家取消");
        return R.ok();
    }

    /**
     * 申请退款
     */
    @PostMapping("/applyRefund")
    public R<Void> applyRefund(@Validated @RequestBody ApplyRefundDto dto) {
        Long userId = super.getUserId();
        appOrderService.applyRefund(userId, dto);
        return R.ok();
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirmReceive")
    public R<Void> confirmReceive(@Validated @RequestBody ConfirmReceiveDto dto) {
        Long userId = super.getUserId();
        appOrderService.confirmReceive(userId, dto, "买家签收");
        return R.ok();
    }
}
