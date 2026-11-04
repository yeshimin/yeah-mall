package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqProperties;
import io.lettuce.core.XAutoClaimArgs;
import io.lettuce.core.models.stream.ClaimedMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Redis Stream Pending 消息认领器：优先使用 XAUTOCLAIM，不支持时回退到 XPENDING + XCLAIM。
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "yeah-boot.mq", name = "impl",
        havingValue = MqProperties.IMPL_REDIS, matchIfMissing = true)
public class RedisStreamPendingMessageClaimer implements RedisPendingMessageClaimer {

    private static final String START_ID = "0-0";
    static final int REVISIT_SCAN_FREQUENCY = 10;

    private final StringRedisTemplate redisTemplate;
    private final AtomicReference<AutoClaimCapability> autoClaimCapability =
            new AtomicReference<>(AutoClaimCapability.UNKNOWN);
    private final Map<String, String> autoClaimCursors = new ConcurrentHashMap<>();
    private final Map<String, String> pendingCursors = new ConcurrentHashMap<>();
    private final Map<String, Long> scanCounts = new ConcurrentHashMap<>();

    /**
     * 创建 Redis Stream Pending 消息认领器。
     */
    public RedisStreamPendingMessageClaimer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<RedisClaimedMessage> claim(String stream, String group, String consumerName,
                                           Duration minIdle, int count) {
        String cursorKey = this.cursorKey(stream, group, consumerName);
        boolean revisit = this.isRevisitScan(cursorKey);
        if (autoClaimCapability.get() != AutoClaimCapability.UNSUPPORTED) {
            try {
                List<RedisClaimedMessage> claimed = this.autoClaim(
                        stream, group, consumerName, minIdle, count, cursorKey, revisit);
                autoClaimCapability.set(AutoClaimCapability.SUPPORTED);
                return claimed;
            } catch (AutoClaimUnavailableException e) {
                if (autoClaimCapability.getAndSet(AutoClaimCapability.UNSUPPORTED)
                        != AutoClaimCapability.UNSUPPORTED) {
                    log.info("Redis XAUTOCLAIM is unavailable; falling back to XPENDING + XCLAIM");
                }
            }
        }
        return this.claimWithPending(stream, group, consumerName, minIdle, count, cursorKey, revisit);
    }

    /**
     * 使用 XAUTOCLAIM 原子扫描并认领 Pending 消息。
     */
    private List<RedisClaimedMessage> autoClaim(String stream, String group, String consumerName,
                                                Duration minIdle, int count, String cursorKey,
                                                boolean revisit) {
        String startId = revisit ? START_ID : autoClaimCursors.getOrDefault(cursorKey, START_ID);
        ClaimedMessages<byte[], byte[]> claimed;
        try {
            claimed = redisTemplate.execute((RedisCallback<ClaimedMessages<byte[], byte[]>>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                XAutoClaimArgs<byte[]> args = new XAutoClaimArgs<byte[]>()
                        .consumer(io.lettuce.core.Consumer.from(this.bytes(group), this.bytes(consumerName)))
                        .minIdleTime(minIdle)
                        .startId(startId)
                        .count(count);
                if (nativeConnection instanceof io.lettuce.core.api.sync.RedisStreamCommands) {
                    @SuppressWarnings("unchecked")
                    io.lettuce.core.api.sync.RedisStreamCommands<byte[], byte[]> commands =
                            (io.lettuce.core.api.sync.RedisStreamCommands<byte[], byte[]>) nativeConnection;
                    return commands.xautoclaim(this.bytes(stream), args);
                }
                if (nativeConnection instanceof io.lettuce.core.api.async.RedisStreamAsyncCommands) {
                    @SuppressWarnings("unchecked")
                    io.lettuce.core.api.async.RedisStreamAsyncCommands<byte[], byte[]> commands =
                            (io.lettuce.core.api.async.RedisStreamAsyncCommands<byte[], byte[]>) nativeConnection;
                    try {
                        return commands.xautoclaim(this.bytes(stream), args).get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Interrupted while executing XAUTOCLAIM", e);
                    } catch (ExecutionException e) {
                        throw new IllegalStateException("XAUTOCLAIM failed", e.getCause());
                    }
                }
                throw new AutoClaimUnavailableException();
            });
        } catch (RuntimeException e) {
            if (this.containsAutoClaimUnavailable(e) || this.isUnknownAutoClaimCommand(e)) {
                throw new AutoClaimUnavailableException(e);
            }
            throw e;
        }

        if (claimed == null) {
            return Collections.emptyList();
        }
        if (!revisit) {
            this.updateCursor(autoClaimCursors, cursorKey, claimed.getId());
        }

        List<RedisClaimedMessage> result = new ArrayList<>();
        for (io.lettuce.core.StreamMessage<byte[], byte[]> message : claimed.getMessages()) {
            MapRecord<String, String, String> record = this.toMapRecord(stream, message);
            Long deliveryCount = this.findDeliveryCount(stream, group, consumerName, record.getId());
            if (deliveryCount != null) {
                result.add(new RedisClaimedMessage(record, deliveryCount));
            }
        }
        return result;
    }

    /**
     * 使用 XPENDING 和 XCLAIM 兼容认领 Pending 消息。
     */
    private List<RedisClaimedMessage> claimWithPending(String stream, String group, String consumerName,
                                                       Duration minIdle, int count, String cursorKey,
                                                       boolean revisit) {
        StreamOperations<String, String, String> operations = redisTemplate.opsForStream();
        String cursor = revisit ? null : pendingCursors.get(cursorKey);
        Range<?> range = cursor == null
                ? Range.unbounded()
                : Range.rightUnbounded(Range.Bound.exclusive(cursor));
        int scanCount = Math.max(count, Math.min(1000, count * 10));
        PendingMessages pending = operations.pending(stream, group, range, scanCount);
        if (pending.isEmpty()) {
            if (!revisit) {
                pendingCursors.remove(cursorKey);
            }
            return Collections.emptyList();
        }

        List<PendingMessage> candidates = new ArrayList<>();
        String lastExaminedId = null;
        int examinedCount = 0;
        for (PendingMessage message : pending) {
            lastExaminedId = message.getIdAsString();
            examinedCount++;
            if (message.getElapsedTimeSinceLastDelivery().compareTo(minIdle) >= 0) {
                candidates.add(message);
                if (candidates.size() >= count) {
                    break;
                }
            }
        }
        boolean pageFullyExamined = examinedCount == pending.size();
        boolean reachedEnd = pageFullyExamined && pending.size() < scanCount;
        if (!revisit && reachedEnd) {
            pendingCursors.remove(cursorKey);
        } else if (!revisit && lastExaminedId != null) {
            // 达到认领批次上限时，只推进到最后实际检查的位置，不能跳过本页剩余 Pending。
            pendingCursors.put(cursorKey, lastExaminedId);
        }
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        RecordId[] ids = candidates.stream().map(PendingMessage::getId).toArray(RecordId[]::new);
        List<MapRecord<String, String, String>> records = operations.claim(
                stream, group, consumerName, minIdle, ids);
        Map<String, Long> deliveryCounts = candidates.stream().collect(Collectors.toMap(
                PendingMessage::getIdAsString,
                message -> message.getTotalDeliveryCount() + 1,
                Math::max,
                HashMap::new));

        List<RedisClaimedMessage> result = new ArrayList<>();
        for (MapRecord<String, String, String> record : records) {
            Long deliveryCount = deliveryCounts.get(record.getId().getValue());
            if (deliveryCount != null) {
                result.add(new RedisClaimedMessage(record, deliveryCount));
            }
        }
        return result;
    }

    private Long findDeliveryCount(String stream, String group, String consumerName, RecordId id) {
        PendingMessages pending = redisTemplate.opsForStream().pending(
                stream,
                Consumer.from(group, consumerName),
                Range.just(id.getValue()),
                1);
        return pending.isEmpty() ? null : pending.get(0).getTotalDeliveryCount();
    }

    private MapRecord<String, String, String> toMapRecord(
            String stream, io.lettuce.core.StreamMessage<byte[], byte[]> message) {
        Map<String, String> body = new LinkedHashMap<>();
        message.getBody().forEach((key, value) -> body.put(this.string(key), this.string(value)));
        return MapRecord.create(stream, body).withId(RecordId.of(message.getId()));
    }

    private void updateCursor(Map<String, String> cursors, String key, String cursor) {
        if (cursor == null || START_ID.equals(cursor)) {
            cursors.remove(key);
        } else {
            cursors.put(key, cursor);
        }
    }

    private boolean isRevisitScan(String cursorKey) {
        long scanCount = scanCounts.merge(cursorKey, 1L, Long::sum);
        return scanCount % REVISIT_SCAN_FREQUENCY == 0;
    }

    private boolean isUnknownAutoClaimCommand(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                String normalized = message.toLowerCase(Locale.ROOT);
                if (normalized.contains("unknown command") && normalized.contains("xautoclaim")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean containsAutoClaimUnavailable(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof AutoClaimUnavailableException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String cursorKey(String stream, String group, String consumerName) {
        return stream + '\n' + group + '\n' + consumerName;
    }

    private byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private String string(byte[] value) {
        return value == null ? "" : new String(value, StandardCharsets.UTF_8);
    }

    enum AutoClaimCapability {
        UNKNOWN,
        SUPPORTED,
        UNSUPPORTED
    }

    /**
     * 当前连接或 Redis 服务不支持 XAUTOCLAIM。
     */
    static class AutoClaimUnavailableException extends RuntimeException {

        AutoClaimUnavailableException() {
        }

        AutoClaimUnavailableException(Throwable cause) {
            super(cause);
        }
    }
}
