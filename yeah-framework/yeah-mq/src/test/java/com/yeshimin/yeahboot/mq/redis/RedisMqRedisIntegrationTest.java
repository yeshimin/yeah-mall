package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;

@EnabledIf("redisServerAvailable")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisMqRedisIntegrationTest {

    private static final String GROUP = "integration-group";

    private Path redisDirectory;
    private Process redisProcess;
    private LettuceConnectionFactory connectionFactory;
    private StringRedisTemplate redisTemplate;

    static boolean redisServerAvailable() {
        try {
            Process process = new ProcessBuilder("redis-server", "--version")
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return false;
            }
            return process.exitValue() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @BeforeAll
    void startRedis() throws Exception {
        redisDirectory = Files.createTempDirectory("yeah-mq-redis-");
        int port = findAvailablePort();
        redisProcess = new ProcessBuilder(
                "redis-server",
                "--bind", "127.0.0.1",
                "--port", String.valueOf(port),
                "--save", "",
                "--appendonly", "no",
                "--dir", redisDirectory.toString())
                .redirectErrorStream(true)
                .redirectOutput(redisDirectory.resolve("redis.log").toFile())
                .start();

        connectionFactory = new LettuceConnectionFactory(new RedisStandaloneConfiguration("127.0.0.1", port));
        connectionFactory.afterPropertiesSet();
        redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        awaitRedisReady();
    }

    @BeforeEach
    void flushRedis() {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }

    @AfterAll
    void stopRedis() throws Exception {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
        if (redisProcess != null) {
            redisProcess.destroy();
            if (!redisProcess.waitFor(3, TimeUnit.SECONDS)) {
                redisProcess.destroyForcibly();
            }
        }
        if (redisDirectory != null) {
            try (java.util.stream.Stream<Path> paths = Files.walk(redisDirectory)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // 临时测试文件仅做尽力清理
                    }
                });
            }
        }
    }

    @Test
    void acknowledgedMessageIsAbsentFromPendingRecovery() throws Exception {
        String topic = "integration-acked";
        MapRecord<String, String, String> pending = createPending(topic, "consumer-old");
        redisTemplate.opsForStream().acknowledge(topic, GROUP, pending.getId());

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        List<RedisClaimedMessage> claimed = claimer.claim(
                topic, GROUP, "consumer-new", Duration.ofMillis(10), 10);

        assertTrue(claimed.isEmpty());
        assertEquals(0, redisTemplate.opsForStream().pending(topic, GROUP).getTotalPendingMessages());
    }

    @Test
    void autoClaimAtomicallyMovesOldConsumersPendingMessageToOnlyOneNewConsumer() throws Exception {
        String topic = "integration-auto-claim";
        createPending(topic, "consumer-old");
        Thread.sleep(150);

        RedisStreamPendingMessageClaimer firstClaimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        RedisStreamPendingMessageClaimer secondClaimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<List<RedisClaimedMessage>> first = executor.submit(() -> {
                start.await();
                return firstClaimer.claim(topic, GROUP, "consumer-a", Duration.ofMillis(100), 10);
            });
            Future<List<RedisClaimedMessage>> second = executor.submit(() -> {
                start.await();
                return secondClaimer.claim(topic, GROUP, "consumer-b", Duration.ofMillis(100), 10);
            });
            start.countDown();

            List<RedisClaimedMessage> firstResult = first.get(5, TimeUnit.SECONDS);
            List<RedisClaimedMessage> secondResult = second.get(5, TimeUnit.SECONDS);
            assertEquals(1, firstResult.size() + secondResult.size());
            assertEquals(RedisStreamPendingMessageClaimer.AutoClaimCapability.SUPPORTED,
                    autoClaimCapability(firstClaimer).get());
            assertEquals(RedisStreamPendingMessageClaimer.AutoClaimCapability.SUPPORTED,
                    autoClaimCapability(secondClaimer).get());

            String owner = firstResult.isEmpty() ? "consumer-b" : "consumer-a";
            PendingMessages ownerPending = redisTemplate.opsForStream().pending(
                    topic, Consumer.from(GROUP, owner), Range.unbounded(), 10);
            assertEquals(1, ownerPending.size());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void pendingAndClaimFallbackMovesOldConsumersMessage() throws Exception {
        String topic = "integration-fallback-claim";
        createPending(topic, "consumer-old");
        Thread.sleep(30);

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        AtomicReference<RedisStreamPendingMessageClaimer.AutoClaimCapability> capability =
                autoClaimCapability(claimer);
        capability.set(RedisStreamPendingMessageClaimer.AutoClaimCapability.UNSUPPORTED);

        List<RedisClaimedMessage> claimed = claimer.claim(
                topic, GROUP, "consumer-new", Duration.ofMillis(20), 10);

        assertEquals(1, claimed.size());
        PendingMessages newConsumerPending = redisTemplate.opsForStream().pending(
                topic, Consumer.from(GROUP, "consumer-new"), Range.unbounded(), 10);
        assertEquals(1, newConsumerPending.size());
    }

    @Test
    void persistentFailureWritesDeadLetterAndAcknowledgesOriginal() throws Exception {
        String topic = "integration-dead-letter";
        MapRecord<String, String, String> pending = createPending(topic, "consumer-old");

        MqProperties properties = new MqProperties();
        properties.setDefaultGroup(GROUP);
        properties.setPendingRetryEnabled(true);
        properties.setPendingMinIdle(Duration.ofMillis(20));
        properties.setMaxDeliveries(2);
        properties.setDeadLetterSuffix(":DLQ");
        RedisMqFailureMetadataStore failureMetadataStore =
                new RedisMqFailureMetadataStore(redisTemplate, properties);
        RedisMqPendingRecoverer recoverer = new RedisMqPendingRecoverer(
                redisTemplate, properties, new RedisStreamPendingMessageClaimer(redisTemplate),
                failureMetadataStore);
        MqListener failingListener = new MqListener() {
            @Override
            public void onMessage(MqMessage message) {
                throw new IllegalStateException("sensitive upstream response");
            }

            @Override
            public String getTopic() {
                return topic;
            }
        };
        RedisMqListener adapter = new RedisMqListener(
                failingListener, redisTemplate, properties, "consumer-old", failureMetadataStore);

        adapter.onMessage(pending);
        RedisMqFailureMetadataStore.FailureMetadata firstFailure = failureMetadataStore.get(
                topic, GROUP, pending.getId().getValue());
        assertEquals(1, firstFailure.getFailureCount());
        String metadataKey = invokeMethod(
                failureMetadataStore, "metadataKey", topic, GROUP, pending.getId().getValue());
        assertTrue(metadataKey.startsWith("mq:pending-error:"));
        assertFalse(metadataKey.contains("yeah-boot"));
        assertTrue(redisTemplate.getExpire(metadataKey, TimeUnit.MILLISECONDS) > 0);
        Thread.sleep(50);

        recoverer.recover(topic, "consumer-new",
                new RedisMqListener(
                        failingListener, redisTemplate, properties, "consumer-new", failureMetadataStore));

        assertEquals(0, redisTemplate.opsForStream().pending(topic, GROUP).getTotalPendingMessages());
        StreamOperations<String, String, String> operations = redisTemplate.opsForStream();
        List<MapRecord<String, String, String>> deadLetters =
                operations.range(topic + ":DLQ", Range.unbounded());
        assertEquals(1, deadLetters.size());
        assertEquals("2", deadLetters.get(0).getValue().get("failureCount"));
        assertEquals("2", deadLetters.get(0).getValue().get("deliveryCount"));
        assertEquals("IllegalStateException", deadLetters.get(0).getValue().get("lastError"));
        assertTrue(failureMetadataStore.get(topic, GROUP, pending.getId().getValue()) == null);
    }

    private MapRecord<String, String, String> createPending(String topic, String consumerName) {
        redisTemplate.execute((RedisCallback<String>) connection -> connection.xGroupCreate(
                topic.getBytes(StandardCharsets.UTF_8), GROUP, ReadOffset.latest(), true));
        StreamOperations<String, String, String> operations = redisTemplate.opsForStream();
        RecordId id = operations.add(StreamRecords.mapBacked(
                Collections.singletonMap("payload", "integration-payload")).withStreamKey(topic));
        if (id == null) {
            throw new IllegalStateException("Failed to publish integration message");
        }
        List<MapRecord<String, String, String>> records = operations.read(
                Consumer.from(GROUP, consumerName),
                StreamReadOptions.empty().count(1),
                StreamOffset.create(topic, ReadOffset.lastConsumed()));
        if (records == null || records.size() != 1) {
            throw new IllegalStateException("Failed to create Pending integration message");
        }
        return records.get(0);
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    @SuppressWarnings("unchecked")
    private AtomicReference<RedisStreamPendingMessageClaimer.AutoClaimCapability> autoClaimCapability(
            RedisStreamPendingMessageClaimer claimer) {
        AtomicReference<RedisStreamPendingMessageClaimer.AutoClaimCapability> capability =
                (AtomicReference<RedisStreamPendingMessageClaimer.AutoClaimCapability>)
                        getField(claimer, "autoClaimCapability");
        if (capability == null) {
            fail("autoClaimCapability field not found");
        }
        return capability;
    }

    private void awaitRedisReady() throws InterruptedException {
        RuntimeException lastError = null;
        for (int attempt = 0; attempt < 50; attempt++) {
            try {
                String pong = redisTemplate.execute((RedisCallback<String>) connection -> connection.ping());
                if ("PONG".equals(pong)) {
                    return;
                }
            } catch (RuntimeException e) {
                lastError = e;
            }
            Thread.sleep(50);
        }
        throw new IllegalStateException("Temporary Redis server did not start", lastError);
    }
}
