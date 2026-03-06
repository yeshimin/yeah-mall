package com.yeshimin.yeahboot.mq;

public interface MqPublisher {

    void publish(MqMessage message);
}
