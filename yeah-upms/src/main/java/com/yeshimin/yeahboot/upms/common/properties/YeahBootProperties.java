package com.yeshimin.yeahboot.upms.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah")
public class YeahBootProperties {

    @PostConstruct
    public void init() {
        log.info("init [yeah] properties...captchaEnabled: {}", captchaEnabled);
    }

    /**
     * 是否开启验证码校验
     */
    private Boolean captchaEnabled;
}
