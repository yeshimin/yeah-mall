package com.yeshimin.yeahboot.mq;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MqPropertiesTest {

    @Test
    void pendingRetryIsOptInByDefault() {
        assertFalse(new MqProperties().isPendingRetryEnabled());
    }

    @Test
    void subMillisecondScanIntervalIsRejected() {
        MqProperties properties = new MqProperties();
        properties.setPendingScanInterval(Duration.ofNanos(500_000));

        assertThrows(IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(properties, "init"));
    }

    @Test
    void subMillisecondMinIdleIsRejected() {
        MqProperties properties = new MqProperties();
        properties.setPendingMinIdle(Duration.ofNanos(500_000));

        assertThrows(IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(properties, "init"));
    }

    @Test
    void unsupportedMqImplementationIsRejected() {
        MqProperties properties = new MqProperties();
        properties.setImpl("rabbitmq");

        assertThrows(IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(properties, "init"));
    }

    @Test
    void blankRedisKeyPrefixIsRejected() {
        MqProperties properties = new MqProperties();
        properties.setRedisKeyPrefix(" ");

        assertThrows(IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(properties, "init"));
    }
}
