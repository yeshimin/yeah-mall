package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class RedisMqListenerRegistrarTest {

    private StringRedisTemplate redisTemplate;
    private RedisMqListenerRegistrar registrar;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        MqProperties properties = new MqProperties();
        registrar = new RedisMqListenerRegistrar();
        ReflectionTestUtils.setField(registrar, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(registrar, "properties", properties);
    }

    @Test
    void existingConsumerGroupIsIgnored() {
        when(redisTemplate.execute(any(RedisCallback.class)))
                .thenThrow(new IllegalStateException("BUSYGROUP Consumer Group name already exists"));

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(
                registrar, "ensureConsumerGroup", "test-topic"));
    }

    @Test
    void realInitializationFailureIsPropagated() {
        when(redisTemplate.execute(any(RedisCallback.class)))
                .thenThrow(new IllegalStateException("connection refused"));

        assertThrows(IllegalStateException.class, () -> ReflectionTestUtils.invokeMethod(
                registrar, "ensureConsumerGroup", "test-topic"));
    }

    @Test
    void duplicateTopicListenersAreRejected() {
        MqListener first = mock(MqListener.class);
        MqListener second = mock(MqListener.class);
        when(first.getTopic()).thenReturn("same-topic");
        when(second.getTopic()).thenReturn("same-topic");
        ReflectionTestUtils.setField(registrar, "listeners", Arrays.asList(first, second));

        assertThrows(IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(registrar, "validateListeners"));
    }
}
