package com.yeshimin.yeahboot.mq.redis;

import com.yeshimin.yeahboot.mq.MqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

@Slf4j
@Configuration
@RequiredArgsConstructor
//@ConditionalOnProperty(prefix = "yeah-boot.mq", name = "impl", havingValue = "redis")
public class RedisMqConfiguration {

    @Bean
    public RedisMqPublisher redisMqPublisher(StringRedisTemplate redisTemplate) {
        return new RedisMqPublisher(redisTemplate);
    }

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer(
            RedisConnectionFactory factory, MqProperties properties) {
        StreamMessageListenerContainer.
                StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(properties.getBatchSize())
                        .pollTimeout(properties.getBlockTimeout())
                        .build();
        return StreamMessageListenerContainer.create(factory, options);
    }
}
