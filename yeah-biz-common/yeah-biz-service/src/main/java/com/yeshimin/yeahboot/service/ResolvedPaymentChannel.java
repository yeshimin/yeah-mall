package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.properties.WxPayProperties;
import com.yeshimin.yeahboot.data.common.enums.PaymentCollectionModeEnum;
import com.yeshimin.yeahboot.data.domain.entity.PaymentChannelEntity;
import lombok.Getter;

/**
 * 已解析的支付路由与凭据
 */
@Getter
public class ResolvedPaymentChannel {

    /**
     * 旧版YML平台支付通道编码
     */
    public static final String LEGACY_CHANNEL_CODE = "legacy-platform";

    /**
     * 订单实际选择的支付路由
     */
    private final PaymentChannelEntity route;

    /**
     * 发起请求和验签使用的主通道凭据
     */
    private final PaymentChannelEntity credential;

    /**
     * 旧版YML支付配置
     */
    private final WxPayProperties legacyProperties;

    /**
     * 使用数据库路由和签名凭据创建解析结果
     */
    public ResolvedPaymentChannel(PaymentChannelEntity route, PaymentChannelEntity credential) {
        this.route = route;
        this.credential = credential;
        this.legacyProperties = null;
    }

    /**
     * 使用旧版YML支付配置创建兼容解析结果
     */
    public ResolvedPaymentChannel(WxPayProperties legacyProperties) {
        this.route = null;
        this.credential = null;
        this.legacyProperties = legacyProperties;
    }

    /**
     * 判断是否使用旧版YML配置
     */
    public boolean isLegacy() {
        return legacyProperties != null;
    }

    /**
     * 获取订单保存的路由ID
     */
    public Long getRouteId() {
        return route == null ? null : route.getId();
    }

    /**
     * 获取回调使用的通道编码
     */
    public String getChannelCode() {
        return route == null ? LEGACY_CHANNEL_CODE : route.getChannelCode();
    }

    /**
     * 获取支付收款模式
     */
    public String getCollectionMode() {
        return route == null ? PaymentCollectionModeEnum.PLATFORM_DIRECT.getValue() : route.getCollectionMode();
    }

    /**
     * 获取当前付款人OpenID所属的AppID
     */
    public String getAppId() {
        if (this.isLegacy()) {
            return legacyProperties.getAppId();
        }
        return this.usesSubApp() ? route.getSubAppId() : credential.getAppId();
    }

    /**
     * 获取当前付款人AppID对应的AppSecret
     */
    public String getAppSecret() {
        if (this.isLegacy()) {
            return legacyProperties.getAppSecret();
        }
        return this.usesSubApp() ? route.getSubAppSecret() : credential.getAppSecret();
    }

    /**
     * 获取发起微信支付请求的商户号
     */
    public String getMchId() {
        return this.isLegacy() ? legacyProperties.getMchId() : credential.getMchId();
    }

    /**
     * 获取服务商模式子商户号
     */
    public String getSubMchId() {
        return route == null ? null : route.getSubMchId();
    }

    /**
     * 获取支付回调地址
     */
    public String getPayNotifyUrl() {
        return this.isLegacy() ? legacyProperties.getApi().getPayNotifyUrl() : credential.getPayNotifyUrl();
    }

    /**
     * 获取退款回调地址
     */
    public String getRefundNotifyUrl() {
        return this.isLegacy() ? legacyProperties.getApi().getRefundNotifyUrl() : credential.getRefundNotifyUrl();
    }

    /**
     * 判断服务商路由是否使用子商户AppID
     */
    private boolean usesSubApp() {
        return PaymentCollectionModeEnum.PLATFORM_PARTNER.getValue().equals(this.getCollectionMode())
                && StrUtil.isNotBlank(route.getSubAppId());
    }
}
