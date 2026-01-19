package com.yeshimin.yeahboot.common.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 通知相关配置-阿里云短信
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.notification.aliyun.sms")
public class NotificationAliyunSmsProperties {

    @PostConstruct
    public void init() {
        log.info("init [yeah-boot.notification.aliyun.sms] properties, key: {}, secret: ******, template: {}, sign: {}",
                accessKeyId, templateCode, signName);
    }

    private String accessKeyId;

    private String accessKeySecret;

    private String templateCode;

    private String signName;
}
