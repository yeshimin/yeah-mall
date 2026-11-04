package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 注册 Redis Stream 监听器并管理 Pending 消息扫描任务。
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "yeah-boot.mq", name = "impl",
        havingValue = MqProperties.IMPL_REDIS, matchIfMissing = true)
public class RedisMqListenerRegistrar {

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MqProperties properties;

    @Autowired
    private RedisMqPendingRecoverer pendingRecoverer;

    @Autowired
    private RedisMqFailureMetadataStore failureMetadataStore;

    @Autowired(required = false)
    private List<MqListener> listeners = Collections.emptyList();

    private final String instanceId = UUID.randomUUID().toString().replace("-", "");
    private final List<PendingRegistration> pendingRegistrations = new ArrayList<>();
    private ScheduledExecutorService pendingScanner;

    /**
     * 注册业务监听器并启动消息监听容器和 Pending 扫描任务。
     */
    @PostConstruct
    public void register() {
        this.validateListeners();
        int listenerIndex = 0;
        for (MqListener listener : listeners) {
            String stream = listener.getTopic();

            this.ensureConsumerGroup(stream);

            String consumerName = "yeah-mq-" + instanceId + '-' + listenerIndex++;
            // 用适配器包装业务监听器
            RedisMqListener adapter = new RedisMqListener(
                    listener, redisTemplate, properties, consumerName, failureMetadataStore);

            container.receive(
                    Consumer.from(properties.getDefaultGroup(), consumerName),
                    StreamOffset.create(stream, ReadOffset.lastConsumed()),
                    adapter
            );
            pendingRegistrations.add(new PendingRegistration(stream, consumerName, adapter));
        }

        container.start();
        this.startPendingScanner();
    }

    /**
     * 校验每个 topic 只注册一个业务监听器。
     */
    private void validateListeners() {
        Set<String> topics = new HashSet<>();
        for (MqListener listener : listeners) {
            String topic = listener.getTopic();
            if (topic == null || topic.trim().isEmpty()) {
                throw new IllegalStateException("MQ listener topic must not be blank");
            }
            if (!topics.add(topic)) {
                throw new IllegalStateException("Only one MQ listener may be registered per topic: " + topic);
            }
        }
    }

    /**
     * 确保 topic 对应的 Stream 与消费组已创建。
     */
    private void ensureConsumerGroup(String stream) {
        // 最后一个参数对应 Redis 的 MKSTREAM，不产生初始化业务消息。
        try {
            redisTemplate.execute((RedisCallback<String>) connection -> connection.xGroupCreate(
                    stream.getBytes(StandardCharsets.UTF_8),
                    properties.getDefaultGroup(),
                    ReadOffset.latest(),
                    true));
        } catch (RuntimeException e) {
            if (this.isBusyGroup(e)) {
                log.debug("Redis Stream consumer group already exists, topic={}, group={}",
                        stream, properties.getDefaultGroup());
                return;
            }
            throw e;
        }
    }

    /**
     * 按配置启动独立的 Pending 消息扫描线程。
     */
    private void startPendingScanner() {
        if (!properties.isPendingRetryActive() || pendingRegistrations.isEmpty()) {
            return;
        }

        pendingScanner = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "yeah-mq-pending-" + instanceId.substring(0, 8));
            thread.setDaemon(true);
            return thread;
        });
        long intervalMillis = properties.getPendingScanInterval().toMillis();
        pendingScanner.scheduleWithFixedDelay(
                this::scanPendingSafely,
                intervalMillis,
                intervalMillis,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 逐个扫描已注册监听器的 Pending 消息，避免单个 topic 失败中断本轮扫描。
     */
    private void scanPendingSafely() {
        for (PendingRegistration registration : pendingRegistrations) {
            try {
                pendingRecoverer.recover(
                        registration.getStream(),
                        registration.getConsumerName(),
                        registration.getListener());
            } catch (Exception e) {
                log.error("MQ Pending scan failed, topic={}, consumer={}, error={}",
                        registration.getStream(), registration.getConsumerName(),
                        RedisMqListener.ProcessingResult.summarize(e));
            }
        }
    }

    private boolean isBusyGroup(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.toUpperCase(Locale.ROOT).contains("BUSYGROUP")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * 停止 Pending 扫描任务和消息监听容器。
     */
    @PreDestroy
    public void stop() {
        if (pendingScanner != null) {
            pendingScanner.shutdown();
            try {
                if (!pendingScanner.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("MQ Pending scanner did not stop within timeout");
                    pendingScanner.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        container.stop();
    }

    /**
     * Pending 扫描所需的监听器注册信息。
     */
    private static final class PendingRegistration {

        private final String stream;
        private final String consumerName;
        private final RedisMqListener listener;

        private PendingRegistration(String stream, String consumerName, RedisMqListener listener) {
            this.stream = stream;
            this.consumerName = consumerName;
            this.listener = listener;
        }

        private String getStream() {
            return stream;
        }

        private String getConsumerName() {
            return consumerName;
        }

        private RedisMqListener getListener() {
            return listener;
        }
    }
}
