package com.yeshimin.yeahboot.data.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 秒杀相关配置
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.seckill")
public class SeckillProperties {

    @PostConstruct
    public void init() {
        log.info("init [yeah-boot.seckill] properties, " +
                "orderTimeoutMinutes: {}, payTimeoutMinutes: {}", orderTimeoutMinutes, payTimeoutMinutes);
    }

    private Integer orderTimeoutMinutes;

    private Integer payTimeoutMinutes;
}
