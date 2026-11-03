package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信支付通道表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_payment_channel")
public class PaymentChannelEntity extends ConditionBaseEntity<PaymentChannelEntity> {

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 店铺ID，0表示平台主通道
     */
    private Long shopId;

    /**
     * App构建目标
     */
    private String appTarget;

    /**
     * 支付收款模式：1平台普通商户直收，2服务商代子商户收款，3独立店铺普通商户直收
     */
    private String collectionMode;

    /**
     * 父通道ID，服务商子商户路由指向服务商主通道
     */
    private Long parentId;

    /**
     * 微信应用AppID或服务商AppID
     */
    private String appId;

    /**
     * 微信应用AppSecret
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String appSecret;

    /**
     * 普通商户号或服务商商户号
     */
    private String mchId;

    /**
     * 子商户应用AppID
     */
    private String subAppId;

    /**
     * 子商户应用AppSecret
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String subAppSecret;

    /**
     * 子商户号
     */
    private String subMchId;

    /**
     * 商户API证书序列号
     */
    private String certificateSerialNo;

    /**
     * 商户API私钥原文
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String privateKeyRaw;

    /**
     * 微信支付公钥ID
     */
    private String wechatPayPublicKeyId;

    /**
     * 微信支付公钥原文
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String wechatPayPublicKeyRaw;

    /**
     * 商户APIv3密钥
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String apiV3Key;

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
}
