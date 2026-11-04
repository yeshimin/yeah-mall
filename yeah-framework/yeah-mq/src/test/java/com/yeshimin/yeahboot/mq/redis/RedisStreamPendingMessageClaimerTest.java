package com.yeshimin.yeahboot.mq.redis;

import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XAutoClaimArgs;
import io.lettuce.core.api.sync.RedisStreamCommands;
import io.lettuce.core.models.stream.ClaimedMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisStreamPendingMessageClaimerTest {

    private static final String TOPIC = "test-topic";
    private static final String GROUP = "test-group";
    private static final String NEW_CONSUMER = "consumer-new";

    private StringRedisTemplate redisTemplate;
    private StreamOperations<String, String, String> streamOperations;
    private RedisConnection connection;
    private RedisStreamCommands<byte[], byte[]> nativeCommands;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        streamOperations = mock(StreamOperations.class);
        connection = mock(RedisConnection.class);
        nativeCommands = mock(RedisStreamCommands.class);
        when(redisTemplate.<String, String>opsForStream()).thenReturn(streamOperations);
        when(connection.getNativeConnection()).thenReturn(nativeCommands);
        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(connection);
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void supportedAutoClaimIsPreferred() {
        Map<byte[], byte[]> body = new LinkedHashMap<>();
        body.put(bytes("payload"), bytes("value"));
        StreamMessage<byte[], byte[]> streamMessage = new StreamMessage<>(bytes(TOPIC), "8-0", body);
        when(nativeCommands.xautoclaim(any(byte[].class), any(XAutoClaimArgs.class)))
                .thenReturn(new ClaimedMessages<>("0-0", Collections.singletonList(streamMessage)));
        PendingMessage pending = new PendingMessage(
                RecordId.of("8-0"), Consumer.from(GROUP, NEW_CONSUMER), Duration.ZERO, 2);
        when(streamOperations.pending(eq(TOPIC), eq(Consumer.from(GROUP, NEW_CONSUMER)),
                any(Range.class), eq(1L)))
                .thenReturn(new PendingMessages(GROUP, Collections.singletonList(pending)));

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        List<RedisClaimedMessage> claimed = claimer.claim(
                TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 10);

        assertEquals(1, claimed.size());
        assertEquals("8-0", claimed.get(0).getRecord().getId().getValue());
        assertEquals(2, claimed.get(0).getDeliveryCount());
        verify(streamOperations, times(0)).claim(any(), any(), any(), any(), any(RecordId[].class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void unsupportedAutoClaimFallsBackAndClaimsPreviousConsumersPendingMessage() {
        when(nativeCommands.xautoclaim(any(byte[].class), any(XAutoClaimArgs.class)))
                .thenThrow(new RedisCommandExecutionException("ERR unknown command 'XAUTOCLAIM'"));
        RecordId id = RecordId.of("1-0");
        PendingMessage oldPending = new PendingMessage(
                id, Consumer.from(GROUP, "consumer-old"), Duration.ofMinutes(1), 1);
        when(streamOperations.pending(eq(TOPIC), eq(GROUP), any(Range.class), anyLong()))
                .thenReturn(new PendingMessages(GROUP, Collections.singletonList(oldPending)));
        MapRecord<String, String, String> record = MapRecord.create(
                TOPIC, Collections.singletonMap("payload", "value")).withId(id);
        when(streamOperations.claim(eq(TOPIC), eq(GROUP), eq(NEW_CONSUMER),
                eq(Duration.ofSeconds(30)), any()))
                .thenReturn(Collections.singletonList(record));

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        List<RedisClaimedMessage> first = claimer.claim(
                TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 10);
        List<RedisClaimedMessage> second = claimer.claim(
                TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 10);

        assertEquals(1, first.size());
        assertEquals(2, first.get(0).getDeliveryCount());
        assertEquals(1, second.size());
        verify(nativeCommands, times(1)).xautoclaim(any(byte[].class), any(XAutoClaimArgs.class));
        verify(streamOperations, times(2)).claim(eq(TOPIC), eq(GROUP), eq(NEW_CONSUMER),
                eq(Duration.ofSeconds(30)), any());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void fallbackCursorStopsAtLastExaminedMessageWhenBatchIsFull() {
        when(nativeCommands.xautoclaim(any(byte[].class), any(XAutoClaimArgs.class)))
                .thenThrow(new RedisCommandExecutionException("ERR unknown command 'XAUTOCLAIM'"));
        List<PendingMessage> page = new ArrayList<>();
        for (int index = 1; index <= 20; index++) {
            page.add(new PendingMessage(
                    RecordId.of(index + "-0"), Consumer.from(GROUP, "consumer-old"),
                    Duration.ofMinutes(1), 1));
        }
        when(streamOperations.pending(eq(TOPIC), eq(GROUP), any(Range.class), anyLong()))
                .thenReturn(new PendingMessages(GROUP, page))
                .thenReturn(new PendingMessages(GROUP, Collections.emptyList()));
        when(streamOperations.claim(eq(TOPIC), eq(GROUP), eq(NEW_CONSUMER),
                eq(Duration.ofSeconds(30)), any()))
                .thenReturn(Arrays.asList(
                        MapRecord.create(TOPIC, Collections.singletonMap("payload", "value"))
                                .withId(RecordId.of("1-0")),
                        MapRecord.create(TOPIC, Collections.singletonMap("payload", "value"))
                                .withId(RecordId.of("2-0"))));

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        claimer.claim(TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 2);
        claimer.claim(TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 2);

        org.mockito.ArgumentCaptor<Range> rangeCaptor = org.mockito.ArgumentCaptor.forClass(Range.class);
        verify(streamOperations, times(2)).pending(eq(TOPIC), eq(GROUP), rangeCaptor.capture(), anyLong());
        Range<?> secondRange = rangeCaptor.getAllValues().get(1);
        assertEquals("2-0", secondRange.getLowerBound().getValue().orElse(null));
        assertFalse(secondRange.getLowerBound().isInclusive());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void fallbackUsesIndependentRevisitCursorDuringContinuousBacklog() {
        when(nativeCommands.xautoclaim(any(byte[].class), any(XAutoClaimArgs.class)))
                .thenThrow(new RedisCommandExecutionException("ERR unknown command 'XAUTOCLAIM'"));
        AtomicInteger pageIndex = new AtomicInteger();
        when(streamOperations.pending(eq(TOPIC), eq(GROUP), any(Range.class), anyLong()))
                .thenAnswer(invocation -> {
                    int page = pageIndex.getAndIncrement();
                    List<PendingMessage> messages = new ArrayList<>();
                    for (int offset = 1; offset <= 10; offset++) {
                        int id = page * 10 + offset;
                        messages.add(new PendingMessage(
                                RecordId.of(id + "-0"), Consumer.from(GROUP, "consumer-old"),
                                Duration.ZERO, 1));
                    }
                    return new PendingMessages(GROUP, messages);
                });

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        for (int scan = 0; scan < RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY + 1; scan++) {
            claimer.claim(TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 1);
        }

        org.mockito.ArgumentCaptor<Range> rangeCaptor = org.mockito.ArgumentCaptor.forClass(Range.class);
        verify(streamOperations, times(RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY + 1))
                .pending(eq(TOPIC), eq(GROUP), rangeCaptor.capture(), anyLong());
        List<Range> ranges = rangeCaptor.getAllValues();
        Range<?> revisitRange = ranges.get(RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY - 1);
        Range<?> resumedPrimaryRange = ranges.get(RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY);
        assertFalse(revisitRange.getLowerBound().getValue().isPresent());
        assertEquals("90-0", resumedPrimaryRange.getLowerBound().getValue().orElse(null));
        assertFalse(resumedPrimaryRange.getLowerBound().isInclusive());
    }

    @Test
    @SuppressWarnings("unchecked")
    void autoClaimUsesIndependentRevisitCursorDuringContinuousBacklog() {
        AtomicInteger scanIndex = new AtomicInteger();
        when(nativeCommands.xautoclaim(any(byte[].class), any(XAutoClaimArgs.class)))
                .thenAnswer(invocation -> {
                    int scan = scanIndex.incrementAndGet();
                    return new ClaimedMessages<>(scan * 10 + "-0", Collections.emptyList());
                });

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        for (int scan = 0; scan < RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY + 1; scan++) {
            claimer.claim(TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 1);
        }

        org.mockito.ArgumentCaptor<XAutoClaimArgs<byte[]>> argsCaptor =
                org.mockito.ArgumentCaptor.forClass(XAutoClaimArgs.class);
        verify(nativeCommands, times(RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY + 1))
                .xautoclaim(any(byte[].class), argsCaptor.capture());
        List<XAutoClaimArgs<byte[]>> arguments = argsCaptor.getAllValues();
        assertEquals("0-0", ReflectionTestUtils.getField(
                arguments.get(RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY - 1), "startId"));
        assertEquals("90-0", ReflectionTestUtils.getField(
                arguments.get(RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY), "startId"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void autoClaimRechecksPreviouslyImmatureMessageFromStreamStart() {
        Map<byte[], byte[]> body = new LinkedHashMap<>();
        body.put(bytes("payload"), bytes("value"));
        StreamMessage<byte[], byte[]> oldMessage = new StreamMessage<>(bytes(TOPIC), "1-0", body);
        AtomicInteger scanIndex = new AtomicInteger();
        when(nativeCommands.xautoclaim(any(byte[].class), any(XAutoClaimArgs.class)))
                .thenAnswer(invocation -> {
                    int scan = scanIndex.incrementAndGet();
                    if (scan == RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY) {
                        XAutoClaimArgs<?> args = invocation.getArgument(1);
                        assertEquals("0-0", ReflectionTestUtils.getField(args, "startId"));
                        return new ClaimedMessages<>("100-0", Collections.singletonList(oldMessage));
                    }
                    return new ClaimedMessages<>(scan * 10 + "-0", Collections.emptyList());
                });
        PendingMessage pending = new PendingMessage(
                RecordId.of("1-0"), Consumer.from(GROUP, NEW_CONSUMER), Duration.ZERO, 2);
        when(streamOperations.pending(eq(TOPIC), eq(Consumer.from(GROUP, NEW_CONSUMER)),
                any(Range.class), eq(1L)))
                .thenReturn(new PendingMessages(GROUP, Collections.singletonList(pending)));

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        List<RedisClaimedMessage> result = Collections.emptyList();
        for (int scan = 0; scan < RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY; scan++) {
            result = claimer.claim(TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 1);
        }

        assertEquals(1, result.size());
        assertEquals("1-0", result.get(0).getRecord().getId().getValue());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void fallbackRechecksPreviouslyImmatureMessageFromStreamStart() {
        when(nativeCommands.xautoclaim(any(byte[].class), any(XAutoClaimArgs.class)))
                .thenThrow(new RedisCommandExecutionException("ERR unknown command 'XAUTOCLAIM'"));
        AtomicInteger scanIndex = new AtomicInteger();
        when(streamOperations.pending(eq(TOPIC), eq(GROUP), any(Range.class), anyLong()))
                .thenAnswer(invocation -> {
                    int scan = scanIndex.incrementAndGet();
                    List<PendingMessage> messages = new ArrayList<>();
                    int firstId = scan == RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY
                            ? 1 : (scan - 1) * 10 + 1;
                    Duration idle = scan == RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY
                            ? Duration.ofMinutes(1) : Duration.ZERO;
                    for (int offset = 0; offset < 10; offset++) {
                        messages.add(new PendingMessage(
                                RecordId.of(firstId + offset + "-0"),
                                Consumer.from(GROUP, "consumer-old"), idle, 1));
                    }
                    return new PendingMessages(GROUP, messages);
                });
        MapRecord<String, String, String> oldRecord = MapRecord.create(
                TOPIC, Collections.singletonMap("payload", "value")).withId(RecordId.of("1-0"));
        when(streamOperations.claim(eq(TOPIC), eq(GROUP), eq(NEW_CONSUMER),
                eq(Duration.ofSeconds(30)), any()))
                .thenReturn(Collections.singletonList(oldRecord));

        RedisStreamPendingMessageClaimer claimer = new RedisStreamPendingMessageClaimer(redisTemplate);
        List<RedisClaimedMessage> result = Collections.emptyList();
        for (int scan = 0; scan < RedisStreamPendingMessageClaimer.REVISIT_SCAN_FREQUENCY; scan++) {
            result = claimer.claim(TOPIC, GROUP, NEW_CONSUMER, Duration.ofSeconds(30), 1);
        }

        assertEquals(1, result.size());
        assertEquals("1-0", result.get(0).getRecord().getId().getValue());
    }

    private byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
