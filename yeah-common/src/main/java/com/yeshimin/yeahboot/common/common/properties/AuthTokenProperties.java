package com.yeshimin.yeahboot.common.common.properties;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.token")
public class AuthTokenProperties {

    private final Map<String, Subject> mapSubject = new HashMap<>();

    @PostConstruct
    private void init() {
        log.info("init [auth.token] properties...subjects: {}", JSON.toJSONString(subjects));

        for (Subject subject : subjects) {
            mapSubject.put(subject.getName(), subject);
        }
    }

    @NestedConfigurationProperty
    private JwtProperties jwt;
    private List<Subject> subjects;

    @Data
    public static class Subject {
        private String name;
        private String apiPrefix;
        private JwtProperties jwt;
        private List<Terminal> terminals;
        private Integer maxOnlineTokenCount;
        private Integer maxOnlineTerminalCount;
    }

    @Data
    public static class Terminal {
        private String name;
        private Integer maxOnlineTokenCount;
    }
}
