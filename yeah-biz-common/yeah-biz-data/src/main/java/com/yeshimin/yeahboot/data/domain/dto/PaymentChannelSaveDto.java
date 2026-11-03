package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.common.enums.PaymentCollectionModeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 支付通道保存请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentChannelSaveDto extends BaseDomain {

    /**
     * 通道ID，新增时为空
     */
    private Long id;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 店铺ID，平台主通道传0
     */
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    /**
     * App构建目标
     */
    private String appTarget;

    /**
     * 支付收款模式：1平台普通商户直收，2服务商代子商户收款，3独立店铺普通商户直收
     */
    @EnumValue(enumClass = PaymentCollectionModeEnum.class, message = "支付收款模式不正确")
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
     * 微信应用AppSecret，修改时留空表示保持不变
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
     * 子商户应用AppSecret，修改时留空表示保持不变
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
     * 商户API私钥原文，修改时留空表示保持不变
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String privateKeyRaw;

    /**
     * 微信支付公钥ID
     */
    private String wechatPayPublicKeyId;

    /**
     * 微信支付公钥原文，修改时留空表示保持不变
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String wechatPayPublicKeyRaw;

    /**
     * 商户APIv3密钥，修改时留空表示保持不变
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
