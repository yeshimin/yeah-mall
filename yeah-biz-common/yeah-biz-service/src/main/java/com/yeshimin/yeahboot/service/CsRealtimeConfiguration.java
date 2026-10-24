package com.yeshimin.yeahboot.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 客服跨实例实时消息配置
 */
@Configuration
public class CsRealtimeConfiguration {

    /**
     * 注册客服实时消息广播监听器
     */
    @Bean
    public RedisMessageListenerContainer csRealtimeRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory, CsRealtimeService realtimeService) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(realtimeService, new ChannelTopic(CsRealtimeService.CHANNEL));
        return container;
    }
}
