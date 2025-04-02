package com.yeshimin.yeahboot.upms.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 签发密钥
     */
    private String secret;

    /**
     * 过期时间（秒）
     */
    private Integer expireSeconds;

    /**
     * 时间校验偏差（秒）
     */
    private Integer defaultLeeway;
}
