package com.yeshimin.yeahboot.ws.mq;

import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;

public interface MqService {

    void publish(String topic, String message, SessionKey sk);
}
