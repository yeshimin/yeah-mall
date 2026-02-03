package com.yeshimin.yeahboot.ws.mq.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class DefaultEventProducer {
    private final RingBuffer<DefaultEvent> ringBuffer;

    public DefaultEventProducer(RingBuffer<DefaultEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<DefaultEvent, DefaultEvent> TRANSLATOR =
            (event, sequence, buffer) -> {
//                String data = buffer.getString();
//                String[] arr = data.split(":", 3);
//                event.setTopic(arr[0]);
//                event.setData(arr[1]);
//                event.setKey(arr[2]);
                event.setTopic(buffer.getTopic());
                event.setData(buffer.getData());
                event.setSk(buffer.getSk());
            };

    public void onData(DefaultEvent buffer) {
        ringBuffer.publishEvent(TRANSLATOR, buffer);
    }
}
