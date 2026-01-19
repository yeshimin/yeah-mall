package com.yeshimin.yeahboot.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 物流-聚合数据平台相关配置
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.delivery.juheapi")
public class JuheApiProperties {

    @PostConstruct
    public void init() {
        log.info("init [yeah-boot.delivery.juheapi] properties, " +
                "key: ******, queryExpUrl: {}, queryCompanyUrl: {}", queryExpUrl, queryComUrl);
    }

    // key
    private String key;

    // query-exp-url
    private String queryExpUrl;

    // query-com-url
    private String queryComUrl;
}
