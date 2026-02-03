package com.yeshimin.yeahboot.ws.websocket.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.websocket")
public class WebSocketProperties {

    @PostConstruct
    private void init() {
        log.info("init [yeah-boot.websocket] properties...heartbeat: {}", heartbeat);
    }

    private Heartbeat heartbeat;

    @Data
    public static class Heartbeat {
        // 是否启用心跳检测
        private Boolean enabled;
        // 心跳检测间隔（秒）
        private Integer interval;
        // 心跳检测超时时间（秒）
        private Integer timeout;
    }
}
