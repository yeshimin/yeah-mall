package com.yeshimin.yeahboot.mq;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * 消息队列配置。
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "yeah-boot.mq")
public class MqProperties {

    // Redis MQ 实现标识
    public static final String IMPL_REDIS = "redis";

    @PostConstruct
    private void init() {
        log.info("init [yeah-boot.mq] properties...impl: {}, autoAck: {}, defaultGroup: {}, batchSize: {}, "
                        + "blockTimeout: {}, pendingRetryEnabled: {}, pendingScanInterval: {}, "
                        + "pendingMinIdle: {}, maxDeliveries: {}, deadLetterSuffix: {}, redisKeyPrefix: {}",
                impl, autoAck, defaultGroup, batchSize, blockTimeout, pendingRetryEnabled,
                pendingScanInterval, pendingMinIdle, maxDeliveries, deadLetterSuffix, redisKeyPrefix);

        if (!IMPL_REDIS.equals(impl)) {
            throw new IllegalStateException("yeah-boot.mq.impl currently only supports redis");
        }
        if (batchSize < 1) {
            throw new IllegalStateException("yeah-boot.mq.batch-size must be greater than 0");
        }
        if (pendingScanInterval == null || pendingScanInterval.compareTo(Duration.ofMillis(1)) < 0) {
            throw new IllegalStateException("yeah-boot.mq.pending-scan-interval must be at least 1ms");
        }
        if (pendingMinIdle == null || pendingMinIdle.compareTo(Duration.ofMillis(1)) < 0) {
            throw new IllegalStateException("yeah-boot.mq.pending-min-idle must be at least 1ms");
        }
        if (maxDeliveries < 1) {
            throw new IllegalStateException("yeah-boot.mq.max-deliveries must be greater than 0");
        }
        if (deadLetterSuffix == null || deadLetterSuffix.trim().isEmpty()) {
            throw new IllegalStateException("yeah-boot.mq.dead-letter-suffix must not be blank");
        }
        if (redisKeyPrefix == null || redisKeyPrefix.trim().isEmpty()) {
            throw new IllegalStateException("yeah-boot.mq.redis-key-prefix must not be blank");
        }
        if (pendingRetryEnabled && !autoAck) {
            log.warn("Pending retry is disabled because yeah-boot.mq.auto-ack=false; "
                    + "automatic recovery would otherwise redeliver successfully processed messages indefinitely");
        }
    }

    // MQ 实现类型，当前仅支持 redis
    private String impl = IMPL_REDIS;

    private boolean autoAck = true;

    private String defaultGroup = "DEFAULT_GROUP";

    private int batchSize = 10;

    private Duration blockTimeout = Duration.ofSeconds(2);

    // 是否重新认领 Redis Stream 中消费失败的 Pending 消息
    private boolean pendingRetryEnabled = false;

    // Pending 消息扫描间隔
    private Duration pendingScanInterval = Duration.ofSeconds(10);

    // Pending 消息可被其他消费者认领前的最小空闲时间
    private Duration pendingMinIdle = Duration.ofSeconds(30);

    // 最大投递次数，包含首次消费
    private int maxDeliveries = 5;

    // 死信 Stream 名称后缀
    private String deadLetterSuffix = ":DLQ";

    // Redis 内部 Key 前缀
    private String redisKeyPrefix = "mq";

    /**
     * 判断 Pending 消息自动恢复是否启用。
     */
    public boolean isPendingRetryActive() {
        return pendingRetryEnabled && autoAck;
    }
}
