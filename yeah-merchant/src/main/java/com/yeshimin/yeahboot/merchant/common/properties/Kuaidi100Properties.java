package com.yeshimin.yeahboot.merchant.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 物流-快递100相关配置
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.delivery.kuaidi100")
public class Kuaidi100Properties {

    @PostConstruct
    public void init() {
        log.info("init [yeah-boot.delivery.kuaidi100] properties, " +
                "key: {}, customer: {}, secret: ******, userId: {}", key, customer, userId);
    }

    // key
    private String key;

    // customer
    private String customer;

    // secret
    private String secret;

    // user-id
    private String userId;
}
