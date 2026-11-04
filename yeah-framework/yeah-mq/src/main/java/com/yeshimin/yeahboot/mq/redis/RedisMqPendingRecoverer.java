package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理当前消费者重新认领的 Pending 消息，成功时 ACK，失败达到上限时转入死信。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "yeah-boot.mq", name = "impl",
        havingValue = MqProperties.IMPL_REDIS, matchIfMissing = true)
public class RedisMqPendingRecoverer {

    private final StringRedisTemplate redisTemplate;
    private final MqProperties properties;
    private final RedisPendingMessageClaimer claimer;
    private final RedisMqFailureMetadataStore failureMetadataStore;

    /**
     * 认领并恢复指定 Stream 中满足空闲时间要求的 Pending 消息。
     */
    public void recover(String stream, String consumerName, RedisMqListener listener) {
        if (!properties.isPendingRetryActive()) {
            return;
        }

        List<RedisClaimedMessage> claimedMessages = claimer.claim(
                stream,
                properties.getDefaultGroup(),
                consumerName,
                properties.getPendingMinIdle(),
                properties.getBatchSize());

        for (RedisClaimedMessage claimed : claimedMessages) {
            try {
                this.recoverOne(stream, consumerName, listener, claimed);
            } catch (Exception e) {
                log.error("MQ Pending recovery failed, topic={}, messageId={}, consumer={}, deliveryCount={}, "
                                + "error={}",
                        stream, claimed.getRecord().getId().getValue(), consumerName,
                        claimed.getDeliveryCount(), RedisMqListener.ProcessingResult.summarize(e));
            }
        }
    }

    /**
     * 恢复单条已认领的 Pending 消息。
     */
    private void recoverOne(String stream, String consumerName, RedisMqListener listener,
                            RedisClaimedMessage claimed) {
        MapRecord<String, String, String> record = claimed.getRecord();
        long deliveryCount = claimed.getDeliveryCount();
        RedisMqFailureMetadataStore.FailureMetadata metadata = failureMetadataStore.get(
                stream, properties.getDefaultGroup(), record.getId().getValue());
        if (metadata != null && metadata.isDeadLetterPending()) {
            this.moveToDeadLetter(stream, consumerName, listener, record, deliveryCount, metadata);
            return;
        }

        RedisMqListener.ProcessingResult result = listener.process(record);
        if (result.isSuccess()) {
            listener.acknowledge(record);
            failureMetadataStore.delete(stream, properties.getDefaultGroup(), record.getId().getValue());
            return;
        }

        listener.logFailure(record, deliveryCount, result.getError());
        metadata = failureMetadataStore.recordFailure(
                stream,
                properties.getDefaultGroup(),
                record.getId().getValue(),
                RedisMqListener.ProcessingResult.summarize(result.getError()),
                deliveryCount >= properties.getMaxDeliveries());
        if (deliveryCount < properties.getMaxDeliveries()) {
            return;
        }

        this.moveToDeadLetter(stream, consumerName, listener, record, deliveryCount, metadata);
    }

    /**
     * 将消息写入死信 Stream，成功后确认原消息并清理失败元数据。
     */
    private void moveToDeadLetter(String stream, String consumerName, RedisMqListener listener,
                                  MapRecord<String, String, String> record, long deliveryCount,
                                  RedisMqFailureMetadataStore.FailureMetadata metadata) {
        String deadLetterStream = stream + properties.getDeadLetterSuffix();
        RecordId deadLetterId = this.writeDeadLetter(
                deadLetterStream, stream, record, metadata.getFailureCount(), deliveryCount,
                metadata.getLastError());
        log.warn("MQ dead-letter message created, topic={}, deadLetterStream={}, originalMessageId={}, "
                        + "deadLetterMessageId={}, consumer={}, failureCount={}, deliveryCount={}, error={}",
                stream, deadLetterStream, record.getId().getValue(), deadLetterId.getValue(), consumerName,
                metadata.getFailureCount(), deliveryCount, metadata.getLastError());
        listener.acknowledge(record);
        failureMetadataStore.delete(stream, properties.getDefaultGroup(), record.getId().getValue());
    }

    /**
     * 写入包含原消息信息、失败次数和安全错误类型的死信消息。
     */
    private RecordId writeDeadLetter(String deadLetterStream, String stream,
                                     MapRecord<String, String, String> record,
                                     long failureCount, long deliveryCount, String lastError) {
        Map<String, String> deadLetter = new LinkedHashMap<>();
        deadLetter.put("originalTopic", stream);
        deadLetter.put("originalMessageId", record.getId().getValue());
        deadLetter.put("payload", this.defaultString(record.getValue().get("payload")));
        deadLetter.put("failureCount", String.valueOf(failureCount));
        deadLetter.put("deliveryCount", String.valueOf(deliveryCount));
        deadLetter.put("lastError", lastError);

        RecordId deadLetterId = redisTemplate.opsForStream().add(
                StreamRecords.mapBacked(deadLetter).withStreamKey(deadLetterStream));
        if (deadLetterId == null) {
            throw new IllegalStateException("Failed to append dead-letter message");
        }
        return deadLetterId;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}

/**
 * Redis Stream Pending 消息认领接口。
 */
interface RedisPendingMessageClaimer {

    /**
     * 原子认领满足最小空闲时间要求的 Pending 消息。
     */
    List<RedisClaimedMessage> claim(String stream, String group, String consumerName,
                                    java.time.Duration minIdle, int count);
}

/**
 * 已认领的 Redis Stream 消息及其投递次数。
 */
final class RedisClaimedMessage {

    private final MapRecord<String, String, String> record;
    private final long deliveryCount;

    RedisClaimedMessage(MapRecord<String, String, String> record, long deliveryCount) {
        this.record = record;
        this.deliveryCount = deliveryCount;
    }

    MapRecord<String, String, String> getRecord() {
        return record;
    }

    long getDeliveryCount() {
        return deliveryCount;
    }
}
