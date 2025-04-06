package com.yeshimin.yeahboot.upms.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot")
public class YeahBootProperties {

    /**
     * 是否开启验证码校验
     */
    private Boolean captchaEnabled;
}
