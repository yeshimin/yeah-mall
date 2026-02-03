package com.yeshimin.yeahboot.ws.mq;

import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;

/**
 * 主题消费者
 */
public interface TopicConsumer {

    void consume(String topic, String message, SessionKey sk);

    String getTopic();
}
