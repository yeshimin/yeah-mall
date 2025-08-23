package com.yeshimin.yeahboot.merchant.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "manticore-search")
public class ManticoreSearchProperties {

    @PostConstruct
    public void init() {
        log.info("init [manticore-search] properties..." + "basePath: {}", basePath);
    }

    private String basePath;
}
