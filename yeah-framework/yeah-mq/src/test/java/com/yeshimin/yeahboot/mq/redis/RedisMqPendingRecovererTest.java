package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisMqPendingRecovererTest {

    private static final String TOPIC = "test-topic";
    private static final String GROUP = "test-group";

    private StringRedisTemplate redisTemplate;
    private StreamOperations<String, String, String> streamOperations;
    private MqProperties properties;
    private RedisMqFailureMetadataStore failureMetadataStore;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        streamOperations = mock(StreamOperations.class);
        failureMetadataStore = mock(RedisMqFailureMetadataStore.class);
        when(redisTemplate.<String, String>opsForStream()).thenReturn(streamOperations);
        when(streamOperations.acknowledge(any(String.class), any(String.class), any(RecordId[].class)))
                .thenReturn(1L);

        properties = new MqProperties();
        properties.setDefaultGroup(GROUP);
        properties.setPendingRetryEnabled(true);
        properties.setPendingMinIdle(Duration.ofSeconds(1));
        properties.setMaxDeliveries(3);
        properties.setDeadLetterSuffix(":DLQ");
        when(failureMetadataStore.recordFailure(
                any(String.class), any(String.class), any(String.class), any(String.class), any(Boolean.class)))
                .thenAnswer(invocation -> new RedisMqFailureMetadataStore.FailureMetadata(
                        1L, invocation.getArgument(3), invocation.getArgument(4)));
    }

    @Test
    void firstFailureIsRetriedAndAcknowledgedAfterSuccess() {
        MapRecord<String, String, String> record = record("1-0");
        FailingListener businessListener = new FailingListener(1);
        RedisMqListener adapter = adapter(businessListener, "consumer-new");

        adapter.onMessage(record);
        verify(streamOperations, never()).acknowledge(TOPIC, GROUP, record.getId());

        RedisMqPendingRecoverer recoverer = recoverer(singleClaim(record, 2));
        recoverer.recover(TOPIC, "consumer-new", adapter);

        assertEquals(2, businessListener.getAttempts());
        verify(streamOperations).acknowledge(TOPIC, GROUP, record.getId());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void persistentFailureMovesMessageToDeadLetterBeforeAcknowledgingOriginal() {
        MapRecord<String, String, String> record = record("2-0");
        FailingListener businessListener = new FailingListener(Integer.MAX_VALUE);
        RedisMqListener adapter = adapter(businessListener, "consumer-new");
        when(failureMetadataStore.recordFailure(TOPIC, GROUP, "2-0", "IllegalStateException", true))
                .thenReturn(metadata(3, "IllegalStateException", true));
        when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("20-0"));

        RedisMqPendingRecoverer recoverer = recoverer(singleClaim(record, 3));
        recoverer.recover(TOPIC, "consumer-new", adapter);

        org.mockito.ArgumentCaptor<MapRecord> deadLetterCaptor =
                org.mockito.ArgumentCaptor.forClass(MapRecord.class);
        verify(streamOperations).add(deadLetterCaptor.capture());
        MapRecord<String, String, String> deadLetter = deadLetterCaptor.getValue();
        assertEquals(TOPIC + ":DLQ", deadLetter.getStream());
        assertEquals(TOPIC, deadLetter.getValue().get("originalTopic"));
        assertEquals("2-0", deadLetter.getValue().get("originalMessageId"));
        assertEquals("payload-value", deadLetter.getValue().get("payload"));
        assertEquals("3", deadLetter.getValue().get("failureCount"));
        assertEquals("3", deadLetter.getValue().get("deliveryCount"));
        assertEquals("IllegalStateException", deadLetter.getValue().get("lastError"));
        org.mockito.InOrder order = inOrder(streamOperations);
        order.verify(streamOperations).add(any(MapRecord.class));
        order.verify(streamOperations).acknowledge(TOPIC, GROUP, record.getId());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void deadLetterWriteFailureDoesNotAcknowledgeOriginal() {
        MapRecord<String, String, String> record = record("2-1");
        FailingListener businessListener = new FailingListener(Integer.MAX_VALUE);
        RedisMqListener adapter = adapter(businessListener, "consumer-new");
        RedisMqFailureMetadataStore.FailureMetadata metadata =
                metadata(3, "IllegalStateException", true);
        when(failureMetadataStore.get(TOPIC, GROUP, "2-1")).thenReturn(null, metadata);
        when(failureMetadataStore.recordFailure(TOPIC, GROUP, "2-1", "IllegalStateException", true))
                .thenReturn(metadata);
        when(streamOperations.add(any(MapRecord.class)))
                .thenThrow(new IllegalStateException("dead-letter unavailable"))
                .thenReturn(RecordId.of("21-0"));
        AtomicInteger claimIndex = new AtomicInteger();
        RedisPendingMessageClaimer claimer = (stream, group, consumer, minIdle, count) -> {
            int index = claimIndex.getAndIncrement();
            if (index == 0) {
                return Collections.singletonList(new RedisClaimedMessage(record, 3));
            }
            if (index == 1) {
                return Collections.singletonList(new RedisClaimedMessage(record, 4));
            }
            return Collections.emptyList();
        };

        RedisMqPendingRecoverer recoverer = recoverer(claimer);
        recoverer.recover(TOPIC, "consumer-new", adapter);
        verify(streamOperations, never()).acknowledge(TOPIC, GROUP, record.getId());

        recoverer.recover(TOPIC, "consumer-new", adapter);

        assertEquals(1, businessListener.getAttempts());
        verify(streamOperations, times(2)).add(any(MapRecord.class));
        verify(streamOperations).acknowledge(TOPIC, GROUP, record.getId());
        org.mockito.ArgumentCaptor<MapRecord> deadLetterCaptor =
                org.mockito.ArgumentCaptor.forClass(MapRecord.class);
        verify(streamOperations, times(2)).add(deadLetterCaptor.capture());
        MapRecord<String, String, String> retriedDeadLetter = deadLetterCaptor.getAllValues().get(1);
        assertEquals("3", retriedDeadLetter.getValue().get("failureCount"));
        assertEquals("4", retriedDeadLetter.getValue().get("deliveryCount"));
        assertEquals("IllegalStateException", retriedDeadLetter.getValue().get("lastError"));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void sensitiveExceptionMessageIsNotWrittenToDeadLetter() {
        MapRecord<String, String, String> record = record("2-2");
        MqListener sensitiveFailure = new MqListener() {
            @Override
            public void onMessage(MqMessage message) {
                throw new IllegalArgumentException("token=secret-value mobile=13800000000");
            }

            @Override
            public String getTopic() {
                return TOPIC;
            }
        };
        RedisMqListener adapter = adapter(sensitiveFailure, "consumer-new");
        when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("22-0"));

        recoverer(singleClaim(record, 3)).recover(TOPIC, "consumer-new", adapter);

        org.mockito.ArgumentCaptor<MapRecord> deadLetterCaptor =
                org.mockito.ArgumentCaptor.forClass(MapRecord.class);
        verify(streamOperations).add(deadLetterCaptor.capture());
        Map<?, ?> deadLetterValue = (Map<?, ?>) deadLetterCaptor.getValue().getValue();
        String lastError = (String) deadLetterValue.get("lastError");
        assertEquals("IllegalArgumentException", lastError);
        assertFalse(lastError.contains("secret-value"));
        assertFalse(lastError.contains("13800000000"));
    }

    @Test
    void acknowledgedMessageIsNotProcessedWhenClaimReturnsNothing() {
        FailingListener businessListener = new FailingListener(0);
        RedisMqListener adapter = adapter(businessListener, "consumer-new");

        recoverer(emptyClaimer()).recover(TOPIC, "consumer-new", adapter);

        assertEquals(0, businessListener.getAttempts());
        verify(streamOperations, never()).acknowledge(any(String.class), any(String.class), any(RecordId[].class));
    }

    @Test
    void parallelScannersProcessAtomicallyClaimedMessageOnlyOnce() throws Exception {
        MapRecord<String, String, String> record = record("3-0");
        FailingListener businessListener = new FailingListener(0);
        RedisMqListener firstAdapter = adapter(businessListener, "consumer-a");
        RedisMqListener secondAdapter = adapter(businessListener, "consumer-b");
        RedisPendingMessageClaimer atomicClaimer = new ConcurrentSingleClaimer(record);
        RedisMqPendingRecoverer recoverer = recoverer(atomicClaimer);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(() -> recoverer.recover(TOPIC, "consumer-a", firstAdapter));
            Future<?> second = executor.submit(() -> recoverer.recover(TOPIC, "consumer-b", secondAdapter));
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }

        assertEquals(1, businessListener.getAttempts());
        verify(streamOperations, times(1)).acknowledge(TOPIC, GROUP, record.getId());
    }

    @Test
    void disabledPendingRetryKeepsLegacyBehavior() {
        AtomicInteger claims = new AtomicInteger();
        RedisPendingMessageClaimer claimer = (stream, group, consumer, minIdle, count) -> {
            claims.incrementAndGet();
            return Collections.emptyList();
        };
        properties.setPendingRetryEnabled(false);

        recoverer(claimer).recover(TOPIC, "consumer-new",
                adapter(new FailingListener(0), "consumer-new"));

        assertEquals(0, claims.get());
    }

    @Test
    void autoAckFalseDisablesPendingRecoveryToAvoidInfiniteRedelivery() {
        properties.setAutoAck(false);
        properties.setPendingRetryEnabled(true);

        assertFalse(properties.isPendingRetryActive());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void successfulBusinessWithAckFailureIsNotCountedAsBusinessFailure() {
        MapRecord<String, String, String> record = record("4-0");
        FailingListener businessListener = new FailingListener(0);
        RedisMqListener adapter = adapter(businessListener, "consumer-new");
        when(streamOperations.acknowledge(TOPIC, GROUP, record.getId()))
                .thenThrow(new IllegalStateException("ack unavailable"))
                .thenReturn(1L);
        AtomicInteger claimIndex = new AtomicInteger();
        RedisPendingMessageClaimer claimer = (stream, group, consumer, minIdle, count) -> {
            int index = claimIndex.getAndIncrement();
            if (index < 2) {
                return Collections.singletonList(new RedisClaimedMessage(record, 4 + index));
            }
            return Collections.emptyList();
        };

        RedisMqPendingRecoverer recoverer = recoverer(claimer);
        recoverer.recover(TOPIC, "consumer-new", adapter);
        recoverer.recover(TOPIC, "consumer-new", adapter);

        assertEquals(2, businessListener.getAttempts());
        verify(streamOperations, times(2)).acknowledge(TOPIC, GROUP, record.getId());
        verify(streamOperations, never()).add(any(MapRecord.class));
        verify(failureMetadataStore, never()).recordFailure(
                any(String.class), any(String.class), any(String.class), any(String.class), any(Boolean.class));
    }

    private RedisMqPendingRecoverer recoverer(RedisPendingMessageClaimer claimer) {
        return new RedisMqPendingRecoverer(redisTemplate, properties, claimer, failureMetadataStore);
    }

    private RedisMqListener adapter(MqListener listener, String consumerName) {
        return new RedisMqListener(
                listener, redisTemplate, properties, consumerName, failureMetadataStore);
    }

    private RedisMqFailureMetadataStore.FailureMetadata metadata(
            long failureCount, String lastError, boolean deadLetterPending) {
        return new RedisMqFailureMetadataStore.FailureMetadata(
                failureCount, lastError, deadLetterPending);
    }

    private RedisPendingMessageClaimer singleClaim(MapRecord<String, String, String> record, long deliveryCount) {
        AtomicReference<List<RedisClaimedMessage>> claims = new AtomicReference<>(
                Collections.singletonList(new RedisClaimedMessage(record, deliveryCount)));
        return (stream, group, consumer, minIdle, count) -> claims.getAndSet(Collections.emptyList());
    }

    private RedisPendingMessageClaimer emptyClaimer() {
        return (stream, group, consumer, minIdle, count) -> Collections.emptyList();
    }

    private MapRecord<String, String, String> record(String id) {
        return MapRecord.create(TOPIC, Collections.singletonMap("payload", "payload-value"))
                .withId(RecordId.of(id));
    }

    private static final class FailingListener implements MqListener {

        private final int failuresBeforeSuccess;
        private final AtomicInteger attempts = new AtomicInteger();

        private FailingListener(int failuresBeforeSuccess) {
            this.failuresBeforeSuccess = failuresBeforeSuccess;
        }

        @Override
        public void onMessage(MqMessage message) {
            int attempt = attempts.incrementAndGet();
            if (attempt <= failuresBeforeSuccess) {
                throw new IllegalStateException("simulated failure");
            }
        }

        @Override
        public String getTopic() {
            return TOPIC;
        }

        private int getAttempts() {
            return attempts.get();
        }
    }

    private static final class ConcurrentSingleClaimer implements RedisPendingMessageClaimer {

        private final MapRecord<String, String, String> record;
        private final CountDownLatch scanners = new CountDownLatch(2);
        private final AtomicBoolean claimed = new AtomicBoolean();

        private ConcurrentSingleClaimer(MapRecord<String, String, String> record) {
            this.record = record;
        }

        @Override
        public List<RedisClaimedMessage> claim(String stream, String group, String consumerName,
                                               Duration minIdle, int count) {
            scanners.countDown();
            try {
                assertTrue(scanners.await(5, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return claimed.compareAndSet(false, true)
                    ? Collections.singletonList(new RedisClaimedMessage(record, 2))
                    : Collections.emptyList();
        }
    }
}
