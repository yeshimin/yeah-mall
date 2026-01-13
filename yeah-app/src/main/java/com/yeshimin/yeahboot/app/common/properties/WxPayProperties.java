package com.yeshimin.yeahboot.app.common.properties;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.app.common.utils.WxPayUtils;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 微信支付相关配置
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.payment.wxpay")
public class WxPayProperties {

    @PostConstruct
    public void init() {
        log.info("init [yeah-boot.payment.wxpay] properties, appId: {}, mchId: {}, serialNo: {}, wechatPublicKey: {}",
                appId, mchId, certificateSerialNo, wechatPayPublicKeyId);

        // 加载商户私钥
        if (StrUtil.isNotBlank(privateKeyPath)) {
            // 场景：单元测试，如果不做配置，此处会报错，简单以此方法跳过
            if (StrUtil.isNotBlank(privateKeyPath) && !privateKeyPath.startsWith(CommonConsts.VALUE_PLACEHOLDER)) {
                this.privateKey = WxPayUtils.loadPrivateKeyFromPath(privateKeyPath);
            }
        } else if (StrUtil.isNotBlank(privateKeyRaw)) {
            this.privateKey = WxPayUtils.loadPrivateKeyFromString(privateKeyRaw);
        } else {
            throw new IllegalArgumentException("WxPayProperties privateKey is required");
        }
        log.info("WxPayProperties load privateKey success");

        // 加载微信支付公钥
        if (StrUtil.isNotBlank(wechatPayPublicKeyPath)) {
            // 场景：单元测试，如果不做配置，此处会报错，简单以此方法跳过
            if (StrUtil.isNotBlank(wechatPayPublicKeyPath) &&
                    !wechatPayPublicKeyPath.startsWith(CommonConsts.VALUE_PLACEHOLDER)) {
                this.wechatPayPublicKey = WxPayUtils.loadPublicKeyFromPath(wechatPayPublicKeyPath);
            }
        } else if (StrUtil.isNotBlank(wechatPayPublicKeyRaw)) {
            this.wechatPayPublicKey = WxPayUtils.loadPublicKeyFromString(wechatPayPublicKeyRaw);
        } else {
            throw new IllegalArgumentException("WxPayProperties wechatPayPublicKey is required");
        }
        log.info("WxPayProperties load wechatPayPublicKey success");
    }

    // appid
    private String appId;
    // 商户ID
    private String mchId;
    // 证书序列号
    private String certificateSerialNo;
    // 商户私钥-原始文本
    private String privateKeyRaw;
    // 商户私钥-文件路径（优先级高于raw）
    private String privateKeyPath;
    // 微信支付公钥ID
    private String wechatPayPublicKeyId;
    // 微信支付公钥-原始文本
    private String wechatPayPublicKeyRaw;
    // 微信支付公钥-文件路径（优先级高于raw）
    private String wechatPayPublicKeyPath;
    // 商户APIv3密钥
    private String apiV3Key;

    private PrivateKey privateKey;
    private PublicKey wechatPayPublicKey;

    private Api api;

    @Data
    public static class Api {
        private String host;
        private String method;
        private String prepayUrl;
        private String notifyUrl;
    }
}
