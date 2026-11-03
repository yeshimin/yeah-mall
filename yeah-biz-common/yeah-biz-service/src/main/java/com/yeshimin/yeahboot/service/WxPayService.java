package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.TransactionAmount;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.properties.WxPayProperties;
import com.yeshimin.yeahboot.common.utils.WxPayUtils;
import com.yeshimin.yeahboot.data.common.enums.PaymentCollectionModeEnum;
import com.yeshimin.yeahboot.data.domain.entity.MemberWxIdentityEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.PaymentChannelEntity;
import com.yeshimin.yeahboot.data.repository.MemberWxIdentityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxPayService {

    /**
     * TODO 切换生产支付后，改为按订单最终应付金额计算。
     */
    public static final int TEST_PAYMENT_AMOUNT_FEN = 1;

    private static final String ALGORITHM = "SHA256withRSA";
    private static final DateTimeFormatter WECHAT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final WxPayProperties wxPayProperties;
    private final PaymentChannelService paymentChannelService;
    private final MemberWxIdentityRepo memberWxIdentityRepo;

    private final Map<String, PaymentRuntime> runtimeCache = new ConcurrentHashMap<>();

    /**
     * 生成预支付单
     */
    public String prepay(OrderEntity order, String description) {
        ResolvedPaymentChannel channel = paymentChannelService.resolveForOrder(order);
        PaymentRuntime runtime = this.getRuntime(channel);
        // OpenID按会员和当前支付AppID获取，避免平台与独立店铺小程序身份混用。
        MemberWxIdentityEntity identity = memberWxIdentityRepo.findByMemberIdAndAppId(
                order.getMemberId(), channel.getAppId());
        if (identity == null) {
            throw new BaseException("当前账号尚未绑定该小程序微信身份，请重新登录后再试");
        }

        String prepayId;
        try {
            prepayId = this.isPartner(channel)
                    ? this.partnerPrepay(order, description, identity.getOpenId(), channel, runtime)
                    : this.directPrepay(order, description, identity.getOpenId(), channel, runtime);
        } catch (ServiceException e) {
            log.warn("微信预支付失败，订单ID：{}，错误码：{}", order.getId(), e.getErrorCode());
            throw new BaseException("微信支付下单失败，请稍后重试");
        }
        log.info("微信预支付单创建成功，订单ID：{}，支付通道：{}", order.getId(), channel.getChannelCode());
        return prepayId;
    }

    /**
     * 生成微信支付调起参数
     */
    public WxPayInfoVo genWxPayInfo(OrderEntity order, String prepayId) {
        ResolvedPaymentChannel channel = paymentChannelService.resolveForOrder(order);
        PaymentRuntime runtime = this.getRuntime(channel);
        WxPayInfoVo info = new WxPayInfoVo();
        info.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
        info.setNonceStr(WxPayUtils.createNonce(32));
        info.setPackageStr("prepay_id=" + prepayId);
        info.setSignType("RSA");
        // 调起支付签名必须使用当前订单通道对应的AppID和商户私钥。
        String signMessage = channel.getAppId() + "\n" + info.getTimestamp() + "\n"
                + info.getNonceStr() + "\n" + info.getPackageStr() + "\n";
        info.setPaySign(WxPayUtils.sign(signMessage, ALGORITHM, runtime.privateKey));
        return info;
    }

    /**
     * 查询订单
     */
    public Transaction queryOrderByOutTradeNo(OrderEntity order) {
        ResolvedPaymentChannel channel = paymentChannelService.resolveForOrder(order);
        PaymentRuntime runtime = this.getRuntime(channel);
        try {
            if (!this.isPartner(channel)) {
                // 普通商户和店铺直连使用直连商户查单接口。
                QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
                request.setOutTradeNo(order.getOrderNo());
                request.setMchid(channel.getMchId());
                return runtime.directService.queryOrderByOutTradeNo(request);
            }

            // 服务商查单必须同时传服务商商户号和订单所属子商户号。
            com.wechat.pay.java.service.partnerpayments.jsapi.model.QueryOrderByOutTradeNoRequest request =
                    new com.wechat.pay.java.service.partnerpayments.jsapi.model.QueryOrderByOutTradeNoRequest();
            request.setOutTradeNo(order.getOrderNo());
            request.setSpMchid(channel.getMchId());
            request.setSubMchid(channel.getSubMchId());
            return this.convertPartnerTransaction(runtime.partnerService.queryOrderByOutTradeNo(request));
        } catch (ServiceException e) {
            log.warn("微信支付查单失败，订单ID：{}，错误码：{}", order.getId(), e.getErrorCode());
            throw new BaseException("微信支付订单查询失败");
        }
    }

    /**
     * 验证支付或退款通知签名
     */
    public boolean verifySign(String channelCode, String notifyData, String serial,
                              String signature, String timestamp, String nonce) {
        try {
            // 先按回调URL中的通道编码选择微信支付公钥，再校验原始请求体签名。
            PaymentRuntime runtime = this.getRuntime(paymentChannelService.resolveByChannelCode(channelCode));
            Headers headers = this.buildHeaders(serial, signature, timestamp, nonce);
            WxPayUtils.validateNotification(serial, runtime.publicKey, headers, notifyData);
            return true;
        } catch (Exception e) {
            log.warn("微信支付通知验签失败，支付通道：{}，异常类型：{}",
                    channelCode, e.getClass().getSimpleName());
            return false;
        }
    }

    /**
     * 解析支付或退款通知
     */
    public WxPayUtils.Notification parseNotification(String channelCode, String notifyData, String serial,
                                                      String signature, String timestamp, String nonce) {
        ResolvedPaymentChannel channel = paymentChannelService.resolveByChannelCode(channelCode);
        PaymentRuntime runtime = this.getRuntime(channel);
        Headers headers = this.buildHeaders(serial, signature, timestamp, nonce);
        return WxPayUtils.parseNotification(runtime.apiV3Key, serial, runtime.publicKey, headers, notifyData);
    }

    /**
     * 申请退款
     */
    public Refund refund(OrderEntity order, String outRefundNo, String reason,
                         long refundAmount, long totalAmount) {
        ResolvedPaymentChannel channel = paymentChannelService.resolveForOrder(order);
        PaymentRuntime runtime = this.getRuntime(channel);
        CreateRequest request = new CreateRequest();
        request.setOutTradeNo(order.getOrderNo());
        request.setOutRefundNo(outRefundNo);
        if (StrUtil.isNotBlank(reason)) {
            request.setReason(reason);
        }
        if (this.isPartner(channel)) {
            // 服务商退款由服务商签名，但退款资金归属必须指定子商户号。
            request.setSubMchid(channel.getSubMchId());
        }
        request.setNotifyUrl(this.withChannelCode(channel.getRefundNotifyUrl(), channel.getChannelCode()));
        AmountReq amount = new AmountReq();
        amount.setRefund(refundAmount);
        amount.setTotal(totalAmount);
        amount.setCurrency("CNY");
        request.setAmount(amount);
        try {
            Refund response = runtime.refundService.create(request);
            log.info("微信退款申请已提交，订单ID：{}，支付通道：{}",
                    order.getId(), channel.getChannelCode());
            return response;
        } catch (ServiceException e) {
            log.warn("微信退款申请失败，订单ID：{}，错误码：{}", order.getId(), e.getErrorCode());
            throw new BaseException("微信退款申请失败，请稍后重试");
        }
    }

    /**
     * 按商户退款单号查询退款状态
     */
    public Refund queryRefundByOutRefundNo(OrderEntity order, String outRefundNo) {
        ResolvedPaymentChannel channel = paymentChannelService.resolveForOrder(order);
        PaymentRuntime runtime = this.getRuntime(channel);
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        request.setOutRefundNo(outRefundNo);
        if (this.isPartner(channel)) {
            request.setSubMchid(channel.getSubMchId());
        }
        try {
            return runtime.refundService.queryByOutRefundNo(request);
        } catch (ServiceException e) {
            log.warn("微信退款查询失败，订单ID：{}，错误码：{}", order.getId(), e.getErrorCode());
            throw new BaseException("微信退款查询失败");
        }
    }

    /**
     * 关闭订单
     */
    public boolean closeOrder(OrderEntity order) {
        ResolvedPaymentChannel channel = paymentChannelService.resolveForOrder(order);
        PaymentRuntime runtime = this.getRuntime(channel);
        try {
            if (this.isPartner(channel)) {
                // 服务商关单使用服务商与子商户组合定位微信订单。
                com.wechat.pay.java.service.partnerpayments.jsapi.model.CloseOrderRequest request =
                        new com.wechat.pay.java.service.partnerpayments.jsapi.model.CloseOrderRequest();
                request.setOutTradeNo(order.getOrderNo());
                request.setSpMchid(channel.getMchId());
                request.setSubMchid(channel.getSubMchId());
                runtime.partnerService.closeOrder(request);
            } else {
                CloseOrderRequest request = new CloseOrderRequest();
                request.setOutTradeNo(order.getOrderNo());
                request.setMchid(channel.getMchId());
                runtime.directService.closeOrder(request);
            }
            return false;
        } catch (ServiceException e) {
            if ("ORDERPAID".equals(e.getErrorCode())) {
                return true;
            }
            log.warn("微信支付关单失败，订单ID：{}，错误码：{}", order.getId(), e.getErrorCode());
            throw new BaseException("微信支付关单失败");
        }
    }

    /**
     * 调用普通商户JSAPI预支付接口
     */
    private String directPrepay(OrderEntity order, String description, String openId,
                                ResolvedPaymentChannel channel, PaymentRuntime runtime) {
        PrepayRequest request = new PrepayRequest();
        request.setAppid(channel.getAppId());
        request.setMchid(channel.getMchId());
        request.setDescription(description);
        request.setOutTradeNo(order.getOrderNo());
        request.setTimeExpire(this.formatTimeExpire(order));
        request.setNotifyUrl(this.withChannelCode(channel.getPayNotifyUrl(), channel.getChannelCode()));
        Amount amount = new Amount();
        amount.setTotal(this.toFen(order.getPayAmount()));
        request.setAmount(amount);
        Payer payer = new Payer();
        payer.setOpenid(openId);
        request.setPayer(payer);
        PrepayResponse response = runtime.directService.prepay(request);
        return response.getPrepayId();
    }

    /**
     * 调用服务商JSAPI预支付接口
     */
    private String partnerPrepay(OrderEntity order, String description, String openId,
                                 ResolvedPaymentChannel channel, PaymentRuntime runtime) {
        PaymentChannelEntity route = channel.getRoute();
        PaymentChannelEntity credential = channel.getCredential();
        com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayRequest request =
                new com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayRequest();
        request.setSpAppid(credential.getAppId());
        request.setSpMchid(credential.getMchId());
        request.setSubAppid(StrUtil.blankToDefault(route.getSubAppId(), null));
        request.setSubMchid(route.getSubMchId());
        request.setDescription(description);
        request.setOutTradeNo(order.getOrderNo());
        request.setTimeExpire(this.formatTimeExpire(order));
        request.setNotifyUrl(this.withChannelCode(channel.getPayNotifyUrl(), channel.getChannelCode()));
        com.wechat.pay.java.service.partnerpayments.jsapi.model.Amount amount =
                new com.wechat.pay.java.service.partnerpayments.jsapi.model.Amount();
        amount.setTotal(this.toFen(order.getPayAmount()));
        request.setAmount(amount);
        com.wechat.pay.java.service.partnerpayments.jsapi.model.Payer payer =
                new com.wechat.pay.java.service.partnerpayments.jsapi.model.Payer();
        if (StrUtil.isNotBlank(route.getSubAppId())) {
            // 独立店铺小程序使用子商户AppID时必须传sub_openid。
            payer.setSubOpenid(openId);
        } else {
            // 平台小程序统一使用服务商AppID下的sp_openid。
            payer.setSpOpenid(openId);
        }
        request.setPayer(payer);
        return runtime.partnerService.prepay(request).getPrepayId();
    }

    /**
     * 获取或创建支付通道运行时客户端
     */
    private PaymentRuntime getRuntime(ResolvedPaymentChannel channel) {
        String cacheKey = this.buildCacheKey(channel);
        return runtimeCache.computeIfAbsent(cacheKey, key -> this.createRuntime(channel));
    }

    /**
     * 根据通道凭据创建微信支付客户端
     */
    private PaymentRuntime createRuntime(ResolvedPaymentChannel channel) {
        try {
            PrivateKey privateKey;
            PublicKey publicKey;
            String serialNo;
            String publicKeyId;
            String apiV3Key;
            if (channel.isLegacy()) {
                // 数据库支付通道启用前的订单继续使用旧版启动配置。
                privateKey = wxPayProperties.getPrivateKey();
                publicKey = wxPayProperties.getWechatPayPublicKey();
                serialNo = wxPayProperties.getCertificateSerialNo();
                publicKeyId = wxPayProperties.getWechatPayPublicKeyId();
                apiV3Key = wxPayProperties.getApiV3Key();
            } else {
                // 数据库仅保存原始密钥文本，构建客户端时再解析为密钥对象。
                PaymentChannelEntity credential = channel.getCredential();
                privateKey = WxPayUtils.loadPrivateKeyFromString(credential.getPrivateKeyRaw());
                publicKey = WxPayUtils.loadPublicKeyFromString(credential.getWechatPayPublicKeyRaw());
                serialNo = credential.getCertificateSerialNo();
                publicKeyId = credential.getWechatPayPublicKeyId();
                apiV3Key = credential.getApiV3Key();
            }
            Config config = new RSAPublicKeyConfig.Builder()
                    .merchantId(channel.getMchId())
                    .privateKey(privateKey)
                    .publicKey(publicKey)
                    .publicKeyId(publicKeyId)
                    .merchantSerialNumber(serialNo)
                    .apiV3Key(apiV3Key)
                    .build();
            JsapiService directService = new JsapiService.Builder().config(config).build();
            com.wechat.pay.java.service.partnerpayments.jsapi.JsapiService partnerService =
                    new com.wechat.pay.java.service.partnerpayments.jsapi.JsapiService.Builder()
                            .config(config).build();
            RefundService refundService = new RefundService.Builder().config(config).build();
            return new PaymentRuntime(privateKey, publicKey, apiV3Key,
                    directService, partnerService, refundService);
        } catch (Exception e) {
            log.warn("支付通道初始化失败，支付通道：{}，异常类型：{}",
                    channel.getChannelCode(), e.getClass().getSimpleName());
            throw new BaseException("支付通道初始化失败");
        }
    }

    /**
     * 构造包含更新时间的缓存键，配置更新后自动创建新客户端
     */
    private String buildCacheKey(ResolvedPaymentChannel channel) {
        if (channel.isLegacy()) {
            return ResolvedPaymentChannel.LEGACY_CHANNEL_CODE;
        }
        PaymentChannelEntity route = channel.getRoute();
        PaymentChannelEntity credential = channel.getCredential();
        return route.getId() + ":" + route.getUpdateTime() + ":"
                + credential.getId() + ":" + credential.getUpdateTime();
    }

    /**
     * 判断当前订单是否使用服务商支付
     */
    private boolean isPartner(ResolvedPaymentChannel channel) {
        return PaymentCollectionModeEnum.PLATFORM_PARTNER.getValue().equals(channel.getCollectionMode());
    }

    /**
     * 将订单实际支付金额转换为微信支付分单位
     */
    private int toFen(BigDecimal amount) {
        BigDecimal actual = amount == null ? BigDecimal.valueOf(TEST_PAYMENT_AMOUNT_FEN, 2) : amount;
        return actual.movePointRight(2).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    /**
     * 在回调地址中附加稳定的支付通道编码
     */
    private String withChannelCode(String notifyUrl, String channelCode) {
        String separator = notifyUrl.contains("?") ? "&" : "?";
        return notifyUrl + separator + "channelCode=" + URLUtil.encodeQuery(channelCode);
    }

    /**
     * 将本地支付超时时间转换为微信支付要求的时区格式
     */
    private String formatTimeExpire(OrderEntity order) {
        if (order.getPayExpireTime() == null) {
            return null;
        }
        return order.getPayExpireTime().atZone(ZoneId.systemDefault()).format(WECHAT_TIME_FORMATTER);
    }

    /**
     * 构造微信支付回调验签请求头
     */
    private Headers buildHeaders(String serial, String signature, String timestamp, String nonce) {
        return new Headers.Builder()
                .add("Wechatpay-Serial", serial)
                .add("Wechatpay-Signature", signature)
                .add("Wechatpay-Timestamp", timestamp)
                .add("Wechatpay-Nonce", nonce)
                .build();
    }

    /**
     * 将服务商查单结果转换为订单服务统一使用的直连交易模型
     */
    private Transaction convertPartnerTransaction(
            com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction source) {
        if (source == null) {
            return null;
        }
        Transaction target = new Transaction();
        target.setAppid(StrUtil.blankToDefault(source.getSubAppid(), source.getSpAppid()));
        target.setMchid(StrUtil.blankToDefault(source.getSubMchid(), source.getSpMchid()));
        target.setOutTradeNo(source.getOutTradeNo());
        target.setTransactionId(source.getTransactionId());
        target.setSuccessTime(source.getSuccessTime());
        target.setTradeStateDesc(source.getTradeStateDesc());
        if (source.getTradeState() != null) {
            target.setTradeState(Transaction.TradeStateEnum.valueOf(source.getTradeState().name()));
        }
        if (source.getAmount() != null) {
            TransactionAmount amount = new TransactionAmount();
            amount.setCurrency(source.getAmount().getCurrency());
            amount.setPayerCurrency(source.getAmount().getPayerCurrency());
            amount.setPayerTotal(source.getAmount().getPayerTotal());
            amount.setTotal(source.getAmount().getTotal());
            target.setAmount(amount);
        }
        return target;
    }

    /**
     * 支付通道运行时客户端集合
     */
    private static class PaymentRuntime {
        private final PrivateKey privateKey;
        private final PublicKey publicKey;
        private final String apiV3Key;
        private final JsapiService directService;
        private final com.wechat.pay.java.service.partnerpayments.jsapi.JsapiService partnerService;
        private final RefundService refundService;

        /**
         * 创建支付通道运行时客户端集合
         */
        private PaymentRuntime(PrivateKey privateKey, PublicKey publicKey, String apiV3Key,
                               JsapiService directService,
                               com.wechat.pay.java.service.partnerpayments.jsapi.JsapiService partnerService,
                               RefundService refundService) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.apiV3Key = apiV3Key;
            this.directService = directService;
            this.partnerService = partnerService;
            this.refundService = refundService;
        }
    }
}
