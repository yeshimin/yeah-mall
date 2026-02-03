package com.yeshimin.yeahboot.ws.mq.disruptor;

import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import lombok.Data;

@Data
public class DefaultEvent {

    private String topic;

    private String data;

    private SessionKey sk;
}
