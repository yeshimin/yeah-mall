package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqPublisher;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisMqPublisher implements MqPublisher {

    private final StringRedisTemplate redisTemplate;

    public RedisMqPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(MqMessage message) {
        redisTemplate.opsForStream().add(StreamRecords.mapBacked(message.toMap()).withStreamKey(message.getTopic()));
    }
}
