package com.yeshimin.yeahboot.ws.mq.disruptor;

import com.lmax.disruptor.EventFactory;

public class DefaultEventFactory implements EventFactory<DefaultEvent> {
    @Override
    public DefaultEvent newInstance() {
        return new DefaultEvent();
    }
}


