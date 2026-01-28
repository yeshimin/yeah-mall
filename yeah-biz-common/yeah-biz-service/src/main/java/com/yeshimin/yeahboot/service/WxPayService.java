package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yeshimin.yeahboot.common.properties.WxPayProperties;
import com.yeshimin.yeahboot.common.utils.WxPayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 微信支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxPayService {

    private final String ALGORITHM = "SHA256withRSA";

    private final WxPayProperties wxPayProperties;

    private Config config;
    private JsapiService jsapiService;
    private RefundService refundService;

    @PostConstruct
    public void init() {
        try {
            // 使用微信支付公钥的RSA配置
            config = new RSAPublicKeyConfig.Builder()
                    .merchantId(wxPayProperties.getMchId())
                    .privateKey(wxPayProperties.getPrivateKey())
                    .publicKey(wxPayProperties.getWechatPayPublicKey())
                    .publicKeyId(wxPayProperties.getWechatPayPublicKeyId())
                    .merchantSerialNumber(wxPayProperties.getCertificateSerialNo())
                    .apiV3Key(wxPayProperties.getApiV3Key())
                    .build();
            jsapiService = new JsapiService.Builder().config(config).build();
            refundService = new RefundService.Builder().config(config).build();
        } catch (Exception e) {
            log.error("WxPayService init error", e);
        }
    }

    /**
     * 生成预支付单
     */
    public PrepayResponse prepay(String description, String outTradeNo) {
        PrepayRequest request = new PrepayRequest();
        request.setAppid(wxPayProperties.getAppId());
        request.setMchid(wxPayProperties.getMchId());
        request.setDescription(description);
        request.setOutTradeNo(outTradeNo);
        request.setNotifyUrl(wxPayProperties.getApi().getPayNotifyUrl());

        // 支付金额
        Amount amount = new Amount();
        amount.setTotal(1); // TODO 暂时写死1分钱
        request.setAmount(amount);
        // 支付者
        Payer payer = new Payer();
        payer.setOpenid("oMslg185RRo-4520_YYSAorqM2i0"); // TODO 暂时写死
        request.setPayer(payer);

        // 调用下单方法，得到应答
        PrepayResponse response = jsapiService.prepay(request);
        log.info("wxpay.prepay, req: {}, resp: {}", JSON.toJSONString(request), JSON.toJSONString(response));
        return response;
    }

    /**
     * 生成微信支付信息
     */
    public WxPayInfoVo genWxPayInfo(String prepayId) {
        WxPayInfoVo info = new WxPayInfoVo();
        info.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
        info.setNonceStr(WxPayUtils.createNonce(32));
        info.setPackageStr("prepay_id=" + prepayId);
        info.setSignType("RSA");
        String signMessage = wxPayProperties.getAppId() + "\n" + info.getTimestamp() + "\n" +
                info.getNonceStr() + "\n" + info.getPackageStr() + "\n";
        info.setPaySign(WxPayUtils.sign(
                signMessage, ALGORITHM, wxPayProperties.getPrivateKey()));
        return info;
    }

    /**
     * 查询订单
     */
    public Transaction queryOrderByOutTradeNo(String outTradeNo) {
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setOutTradeNo(outTradeNo);
        request.setMchid(wxPayProperties.getMchId());
        Transaction transaction = jsapiService.queryOrderByOutTradeNo(request);
        log.info("wxpay.queryOrderByOutTradeNo, req: {}, resp: {}",
                JSON.toJSONString(request), JSON.toJSONString(transaction));
        return transaction;
    }

    /**
     * 验证签名
     */
    public boolean verifySign(String notifyData, String serial, String signature, String timestamp, String nonce) {
        boolean success = false;
        try {
            Headers headers = this.buildHeaders(serial, signature, timestamp, nonce);
            WxPayUtils.validateNotification(serial, wxPayProperties.getWechatPayPublicKey(), headers, notifyData);
            success = true;
        } catch (Exception e) {
            log.error("wxpay.verifySign error", e);
        }
        return success;
    }

    /**
     * 解析支付/退款回调通知数据
     */
    public WxPayUtils.Notification parseNotification(
            String notifyData, String serial, String signature, String timestamp, String nonce) {
        // 解密数据
        Headers headers = this.buildHeaders(serial, signature, timestamp, nonce);
        WxPayUtils.Notification notification = WxPayUtils.parseNotification(
                wxPayProperties.getApiV3Key(), serial, wxPayProperties.getWechatPayPublicKey(), headers, notifyData);
        log.info("wxpay.handlePayNotify notification: {}", JSON.toJSONString(notification));
        return notification;
    }

    /**
     * 退款
     * https://pay.weixin.qq.com/doc/v3/merchant/4012791903
     */
    public Refund refund(String outTradeNo, String outRefundNo, String reason, long refundAmount, long totalAmount) {
        CreateRequest request = new CreateRequest();
        // 业务订单编号
        request.setOutTradeNo(outTradeNo);
        // 自定义退款编号
        request.setOutRefundNo(outRefundNo);
        // 退款原因
        if (StrUtil.isNotBlank(reason)) {
            request.setReason(reason);
        }
        // 回调通知地址
        request.setNotifyUrl(wxPayProperties.getApi().getRefundNotifyUrl());
        // 退款金额
        AmountReq amount = new AmountReq();
        amount.setRefund(refundAmount);
        amount.setTotal(totalAmount);
        amount.setCurrency("CNY");
        request.setAmount(amount);

        Refund response = refundService.create(request);
        log.info("wxpay.refund, req: {}, resp: {}", JSON.toJSONString(request), JSON.toJSONString(response));
        return response;
    }

    // ================================================================================

    private Headers buildHeaders(String serial, String signature, String timestamp, String nonce) {
        return new Headers.Builder()
                .add("Wechatpay-Serial", serial)
                .add("Wechatpay-Signature", signature)
                .add("Wechatpay-Timestamp", timestamp)
                .add("Wechatpay-Nonce", nonce)
                .build();
    }
}
