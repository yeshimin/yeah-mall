package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.util.Objects;

@Slf4j
public class RedisMqListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;
    private final MqListener mqListener;
    private final MqProperties properties;

    public RedisMqListener(MqListener mqListener, StringRedisTemplate redisTemplate, MqProperties properties) {
        this.mqListener = mqListener;
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            MqMessage mqMessage = new MqMessage();
            mqMessage.setTopic(mqListener.getTopic());
            String payload = message.getValue().get("payload");
            mqMessage.setPayload(payload);
            mqListener.onMessage(mqMessage);

            if (properties.isAutoAck()) {
                redisTemplate.opsForStream().acknowledge(
                        Objects.requireNonNull(message.getStream()),
                        properties.getDefaultGroup(),
                        message.getId()
                );
            }
        } catch (Exception e) {
            // 不 ACK，等待自动重试
            log.error("消息处理异常", e);
        }
    }
}
