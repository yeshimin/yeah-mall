package com.yeshimin.yeahboot.app.service;

import com.alibaba.fastjson2.JSON;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.yeshimin.yeahboot.app.common.properties.WxPayProperties;
import com.yeshimin.yeahboot.app.common.utils.WxPayUtils;
import com.yeshimin.yeahboot.app.domain.vo.WxPayInfoVo;
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
        request.setNotifyUrl(wxPayProperties.getApi().getNotifyUrl());

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
     * 解析支付回调通知数据
     */
    public WxPayUtils.Notification parsePayNotification(
            String notifyData, String serial, String signature, String timestamp, String nonce) {
        // 解密数据
        Headers headers = this.buildHeaders(serial, signature, timestamp, nonce);
        WxPayUtils.Notification notification = WxPayUtils.parseNotification(
                wxPayProperties.getApiV3Key(), serial, wxPayProperties.getWechatPayPublicKey(), headers, notifyData);
        log.info("wxpay.handlePayNotify notification: {}", JSON.toJSONString(notification));
        return notification;
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
