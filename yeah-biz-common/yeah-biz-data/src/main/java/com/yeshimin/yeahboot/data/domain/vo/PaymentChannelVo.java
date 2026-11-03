package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付通道视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentChannelVo extends BaseDomain {

    /**
     * 通道ID
     */
    private Long id;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * App构建目标
     */
    private String appTarget;

    /**
     * 支付收款模式：1平台普通商户直收，2服务商代子商户收款，3独立店铺普通商户直收
     */
    private String collectionMode;

    /**
     * 父通道ID
     */
    private Long parentId;

    /**
     * 微信应用AppID或服务商AppID
     */
    private String appId;

    /**
     * 普通商户号或服务商商户号
     */
    private String mchId;

    /**
     * 子商户应用AppID
     */
    private String subAppId;

    /**
     * 子商户号
     */
    private String subMchId;

    /**
     * 商户API证书序列号
     */
    private String certificateSerialNo;

    /**
     * 微信支付公钥ID
     */
    private String wechatPayPublicKeyId;

    /**
     * 支付回调地址
     */
    private String payNotifyUrl;

    /**
     * 退款回调地址
     */
    private String refundNotifyUrl;

    /**
     * 是否启用支付通道
     */
    private Boolean isEnabled;

    /**
     * 是否已配置AppSecret
     */
    private Boolean hasAppSecret;

    /**
     * 是否已配置子应用AppSecret
     */
    private Boolean hasSubAppSecret;

    /**
     * 是否已配置商户API私钥
     */
    private Boolean hasPrivateKey;

    /**
     * 是否已配置微信支付公钥
     */
    private Boolean hasWechatPayPublicKey;

    /**
     * 是否已配置APIv3密钥
     */
    private Boolean hasApiV3Key;
}
