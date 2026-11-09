package com.yeshimin.yeahboot.common.properties;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.utils.WxPayUtils;
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

    /**
     * 初始化旧版YML微信支付密钥对象。
     * <p>
     * 配置缺失或密钥文件不存在时不阻断启动，仅记录告警，延迟到实际执行支付相关操作时再报错，
     * 便于他人拉取代码后无需立即配置完整的支付证书即可启动运行。
     */
    @PostConstruct
    public void init() {
        log.info("init [yeah-boot.payment.wxpay] properties, appId: {}, mchId: {}, serialNo: {}, wechatPublicKey: {}",
                appId, mchId, certificateSerialNo, wechatPayPublicKeyId);

        // 加载商户私钥
        if (StrUtil.isNotBlank(privateKeyPath)) {
            // 场景：单元测试，如果不做配置，此处会报错，简单以此方法跳过
            if (StrUtil.isNotBlank(privateKeyPath) && !privateKeyPath.startsWith(CommonConsts.VALUE_PLACEHOLDER)) {
                try {
                    this.privateKey = WxPayUtils.loadPrivateKeyFromPath(privateKeyPath);
                    log.info("WxPayProperties load privateKey success");
                } catch (Exception e) {
                    log.warn("WxPayProperties load privateKey failed, will fail when payment is actually performed, path: {}",
                            privateKeyPath, e);
                }
            }
        } else if (StrUtil.isNotBlank(privateKeyRaw)) {
            try {
                this.privateKey = WxPayUtils.loadPrivateKeyFromString(privateKeyRaw);
                log.info("WxPayProperties load privateKey success");
            } catch (Exception e) {
                log.warn("WxPayProperties load privateKey from raw failed, will fail when payment is actually performed", e);
            }
        } else {
            log.warn("WxPayProperties privateKey is not configured, will fail when payment is actually performed");
        }

        // 加载微信支付公钥
        if (StrUtil.isNotBlank(wechatPayPublicKeyPath)) {
            if (StrUtil.isNotBlank(wechatPayPublicKeyPath) &&
                    !wechatPayPublicKeyPath.startsWith(CommonConsts.VALUE_PLACEHOLDER)) {
                try {
                    this.wechatPayPublicKey = WxPayUtils.loadPublicKeyFromPath(wechatPayPublicKeyPath);
                    log.info("WxPayProperties load wechatPayPublicKey success");
                } catch (Exception e) {
                    log.warn("WxPayProperties load wechatPayPublicKey failed, will fail when payment is actually performed, path: {}",
                            wechatPayPublicKeyPath, e);
                }
            }
        } else if (StrUtil.isNotBlank(wechatPayPublicKeyRaw)) {
            try {
                this.wechatPayPublicKey = WxPayUtils.loadPublicKeyFromString(wechatPayPublicKeyRaw);
                log.info("WxPayProperties load wechatPayPublicKey success");
            } catch (Exception e) {
                log.warn("WxPayProperties load wechatPayPublicKey from raw failed, will fail when payment is actually performed", e);
            }
        } else {
            log.warn("WxPayProperties wechatPayPublicKey is not configured, will fail when payment is actually performed");
        }
    }

    // appid
    private String appId;
    // appSecret，用于小程序登录身份换取
    private String appSecret;
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

    /**
     * 微信支付接口与回调地址配置
     */
    @Data
    public static class Api {
        private String host;
        private String method;
        private String payNotifyUrl;
        private String refundNotifyUrl;
    }
}
