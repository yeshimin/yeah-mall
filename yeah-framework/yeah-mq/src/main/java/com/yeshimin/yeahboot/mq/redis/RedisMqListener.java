package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.util.Objects;

/**
 * Redis Stream 消息监听适配器。
 */
@Slf4j
public class RedisMqListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;
    private final MqListener mqListener;
    private final MqProperties properties;
    private final String consumerName;
    private final RedisMqFailureMetadataStore failureMetadataStore;

    /**
     * 创建使用默认消费者名称的消息监听适配器。
     */
    public RedisMqListener(MqListener mqListener, StringRedisTemplate redisTemplate, MqProperties properties) {
        this(mqListener, redisTemplate, properties, "unknown",
                new RedisMqFailureMetadataStore(redisTemplate, properties));
    }

    /**
     * 创建使用指定消费者名称的消息监听适配器。
     */
    public RedisMqListener(MqListener mqListener, StringRedisTemplate redisTemplate, MqProperties properties,
                           String consumerName) {
        this(mqListener, redisTemplate, properties, consumerName,
                new RedisMqFailureMetadataStore(redisTemplate, properties));
    }

    RedisMqListener(MqListener mqListener, StringRedisTemplate redisTemplate, MqProperties properties,
                    String consumerName, RedisMqFailureMetadataStore failureMetadataStore) {
        this.mqListener = mqListener;
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.consumerName = consumerName;
        this.failureMetadataStore = failureMetadataStore;
    }

    /**
     * 处理新投递的 Stream 消息。
     */
    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        ProcessingResult result = this.process(message);
        if (!result.isSuccess()) {
            this.recordFailure(message, 1, result.getError());
            this.logFailure(message, 1, result.getError());
            return;
        }

        if (!properties.isAutoAck()) {
            return;
        }

        try {
            this.acknowledge(message);
        } catch (Exception e) {
            this.logFailure(message, 1, e);
        }
    }

    /**
     * 调用业务监听器处理消息并返回处理结果。
     */
    ProcessingResult process(MapRecord<String, String, String> message) {
        try {
            MqMessage mqMessage = new MqMessage();
            mqMessage.setTopic(mqListener.getTopic());
            mqMessage.setPayload(message.getValue().get("payload"));
            mqListener.onMessage(mqMessage);
            return ProcessingResult.success();
        } catch (Exception e) {
            return ProcessingResult.failure(e);
        }
    }

    /**
     * 确认消息已完成消费。
     */
    void acknowledge(MapRecord<String, String, String> message) {
        redisTemplate.opsForStream().acknowledge(
                Objects.requireNonNull(message.getStream()),
                properties.getDefaultGroup(),
                message.getId()
        );
    }

    /**
     * 记录不包含敏感异常正文的消费失败日志。
     */
    void logFailure(MapRecord<String, String, String> message, long deliveryCount, Throwable error) {
        log.warn("MQ message processing failed, topic={}, messageId={}, consumer={}, deliveryCount={}, error={}",
                mqListener.getTopic(), message.getId().getValue(), consumerName, deliveryCount,
                ProcessingResult.summarize(error));
    }

    /**
     * 记录消息业务处理失败次数和安全错误类型。
     */
    private void recordFailure(MapRecord<String, String, String> message, long deliveryCount, Throwable error) {
        if (!properties.isPendingRetryActive()) {
            return;
        }
        try {
            failureMetadataStore.recordFailure(
                    mqListener.getTopic(),
                    properties.getDefaultGroup(),
                    message.getId().getValue(),
                    ProcessingResult.summarize(error),
                    deliveryCount >= properties.getMaxDeliveries());
        } catch (Exception metadataError) {
            log.error("MQ failure metadata update failed, topic={}, messageId={}, consumer={}, error={}",
                    mqListener.getTopic(), message.getId().getValue(), consumerName,
                    ProcessingResult.summarize(metadataError));
        }
    }

    /**
     * 单次业务消息处理结果。
     */
    static final class ProcessingResult {

        private final boolean success;
        private final Throwable error;

        private ProcessingResult(boolean success, Throwable error) {
            this.success = success;
            this.error = error;
        }

        static ProcessingResult success() {
            return new ProcessingResult(true, null);
        }

        static ProcessingResult failure(Throwable error) {
            return new ProcessingResult(false, error);
        }

        boolean isSuccess() {
            return success;
        }

        Throwable getError() {
            return error;
        }

        static String summarize(Throwable error) {
            if (error == null) {
                return "UnknownError";
            }
            Throwable root = error;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            String type = error.getClass().getSimpleName();
            String rootType = root.getClass().getSimpleName();
            return type.equals(rootType) ? type : type + " (caused by " + rootType + ')';
        }
    }
}
