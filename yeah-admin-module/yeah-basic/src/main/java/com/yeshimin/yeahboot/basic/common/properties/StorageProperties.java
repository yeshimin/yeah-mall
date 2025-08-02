package com.yeshimin.yeahboot.basic.common.properties;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.storage")
public class StorageProperties {

    @PostConstruct
    public void init() {
        log.info("init [yeah-boot.storage] properties... enabled: {}, impl: {}", enabled, impl);

        // 检查local.base-path是否为空
        if (StrUtil.isBlank(impl.getLocal().getBasePath())) {
            throw new IllegalArgumentException("Local storage base path must not be empty");
        }
        // 检查priority是否为空
        if (impl.getLocal().getPriority() == null ||
                impl.getQiniu().getPriority() == null ||
                impl.getMinio().getPriority() == null) {
            throw new IllegalArgumentException("Storage priority must not be null");
        }
    }

    /**
     * 是否启用
     * 暂时没用到
     */
    private Boolean enabled;

    /**
     * 实现
     */
    private Impl impl;

    /**
     * 业务
     */
    private Biz biz;

    @Data
    public static class Impl {
        private LocalImpl local;
        private QiniuImpl qiniu;
        private MinioImpl minio;
    }

    @Data
    public static class LocalImpl {
        private Boolean enabled;
        private Integer priority;
        private String basePath;
    }

    @Data
    public static class QiniuImpl {
        private Boolean enabled;
        private Integer priority;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;
    }

    @Data
    public static class MinioImpl {
        private Boolean enabled;
        private Integer priority;
        private String endpoint;
        private String bucketName;
        private String accessKey;
        private String secretKey;
    }

    // ================================================================================
    // biz file

    @Data
    public static class Biz {
        private BizFile file;
    }

    @Data
    public static class BizFile {
        private String bucket;
        private String path;
    }
}
