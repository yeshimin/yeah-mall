package com.yeshimin.yeahboot.common.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot")
public class YeahBootProperties {

    @PostConstruct
    public void init() {
        log.info("init [yeah-boot] properties...captchaEnabled: {}, safeMode: {}",
                captchaEnabled, safeMode);
    }

    /**
     * 是否开启验证码校验
     */
    private Boolean captchaEnabled;

    /**
     * 是否安全模式
     */
    private Boolean safeMode;
}
