package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

/**
 * 持久化单条 Pending 消息的业务失败次数和安全错误类型。
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "yeah-boot.mq", name = "impl",
        havingValue = MqProperties.IMPL_REDIS, matchIfMissing = true)
public class RedisMqFailureMetadataStore {

    private static final String FAILURE_METADATA_KEY = "pending-error";
    private static final Duration METADATA_TTL = Duration.ofDays(30);
    private static final DefaultRedisScript<Long> RECORD_FAILURE_SCRIPT = new DefaultRedisScript<>(
            "local count = redis.call('HINCRBY', KEYS[1], 'failureCount', 1); "
                    + "redis.call('HSET', KEYS[1], 'lastError', ARGV[1]); "
                    + "if ARGV[2] == '1' then "
                    + "redis.call('HSET', KEYS[1], 'deadLetterPending', '1'); end; "
                    + "redis.call('PEXPIRE', KEYS[1], ARGV[3]); return count;",
            Long.class);

    private final StringRedisTemplate redisTemplate;
    private final MqProperties properties;

    /**
     * 原子累加业务失败次数，并刷新错误类型、死信状态和过期时间。
     */
    FailureMetadata recordFailure(String stream, String group, String messageId,
                                  String lastError, boolean deadLetterPending) {
        String key = this.metadataKey(stream, group, messageId);
        Long failureCount = redisTemplate.execute(
                RECORD_FAILURE_SCRIPT,
                Collections.singletonList(key),
                lastError,
                deadLetterPending ? "1" : "0",
                String.valueOf(METADATA_TTL.toMillis()));
        if (failureCount == null) {
            throw new IllegalStateException("Failed to persist MQ failure metadata");
        }
        return new FailureMetadata(failureCount, lastError, deadLetterPending);
    }

    /**
     * 获取并续期指定消息的失败元数据。
     */
    FailureMetadata get(String stream, String group, String messageId) {
        String key = this.metadataKey(stream, group, messageId);
        Map<String, String> values = redisTemplate.<String, String>opsForHash().entries(key);
        if (values.isEmpty()) {
            return null;
        }
        redisTemplate.expire(key, METADATA_TTL);
        return new FailureMetadata(
                this.parseFailureCount(values.get("failureCount")),
                this.defaultString(values.get("lastError"), "UnknownProcessingError"),
                "1".equals(values.get("deadLetterPending")));
    }

    /**
     * 删除指定消息的失败元数据。
     */
    void delete(String stream, String group, String messageId) {
        redisTemplate.delete(this.metadataKey(stream, group, messageId));
    }

    private String metadataKey(String stream, String group, String messageId) {
        return properties.getRedisKeyPrefix() + ':' + FAILURE_METADATA_KEY + ':'
                + this.encode(stream) + ':' + this.encode(group) + ':' + messageId;
    }

    private long parseFailureCount(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid MQ failure metadata", e);
        }
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 单条消息的业务失败元数据。
     */
    static final class FailureMetadata {

        private final long failureCount;
        private final String lastError;
        private final boolean deadLetterPending;

        FailureMetadata(long failureCount, String lastError, boolean deadLetterPending) {
            this.failureCount = failureCount;
            this.lastError = lastError;
            this.deadLetterPending = deadLetterPending;
        }

        long getFailureCount() {
            return failureCount;
        }

        String getLastError() {
            return lastError;
        }

        boolean isDeadLetterPending() {
            return deadLetterPending;
        }
    }
}
