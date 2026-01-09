package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.yeshimin.yeahboot.app.domain.dto.OrderNoDto;
import com.yeshimin.yeahboot.app.domain.dto.OrderPreviewDto;
import com.yeshimin.yeahboot.app.domain.dto.OrderQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.OrderSubmitDto;
import com.yeshimin.yeahboot.app.domain.vo.OrderCountVo;
import com.yeshimin.yeahboot.app.domain.vo.OrderShopVo;
import com.yeshimin.yeahboot.app.domain.vo.WxPayInfoVo;
import com.yeshimin.yeahboot.app.service.AppOrderService;
import com.yeshimin.yeahboot.app.service.AppOrderStatusService;
import com.yeshimin.yeahboot.app.service.WxPayService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final AppOrderStatusService appOrderStatusService;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public R<Void> submit(@Validated @RequestBody OrderSubmitDto dto) {
        Long userId = super.getUserId();
        appOrderService.submit(userId, dto);
        return R.ok();
    }

    /**
     * 生成支付信息
     */
    @PostMapping("/genPayInfo")
    public R<WxPayInfoVo> genPayInfo(@Validated @RequestBody OrderNoDto dto) {
        Long userId = super.getUserId();
        WxPayInfoVo payInfoVo = appOrderService.genPayInfo(userId, dto);
        return R.ok(payInfoVo);
    }

    /**
     * 查询支付订单信息
     */
    @GetMapping("queryPayOrderInfo")
    public R<Transaction> queryPayOrderInfo(@RequestParam("orderNo") String orderNo) {
        Long userId = super.getUserId();
        Transaction result = appOrderService.queryPayOrderInfo(userId, orderNo);
        return R.ok(result);
    }

    /**
     * 微信支付通知
     * https://pay.weixin.qq.com/doc/v3/merchant/4012791861
     */
    @PublicAccess
    @PostMapping("/wxpay/notify")
    public ResponseEntity<Void> wxPayNotify(
            @RequestBody(required = false) String notifyData,
            @RequestHeader(value = "Wechatpay-Serial", required = false) String serial,
            @RequestHeader(value = "Wechatpay-Signature", required = false) String signature,
            @RequestHeader(value = "Wechatpay-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "Wechatpay-Nonce", required = false) String nonce) {
        log.info("wxpay.notify serial: {}, signature: {}, timestamp: {}, nonce: {}",
                serial, signature, timestamp, nonce);

        // 验证签名
        boolean verifySuccess = wxPayService.verifySign(notifyData, serial, signature, timestamp, nonce);
        if (!verifySuccess) {
            log.warn("微信支付通知验签失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 处理业务
        boolean handleSuccess = false;
        try {
            handleSuccess = appOrderService.handlePayNotify(notifyData, serial, signature, timestamp, nonce);
        } catch (Exception e) {
            log.error("微信支付通知处理异常", e);
        }
        if (!handleSuccess) {
            log.warn("微信支付通知处理失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 查询个人订单
     */
    @GetMapping("/query")
    public R<IPage<OrderShopVo>> query(Page<OrderEntity> page, @Validated OrderQueryDto dto) {
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
        Long userId = WebContextUtils.getUserId();
        return R.ok(appOrderService.preview(userId, dto));
    }
}
