package com.yeshimin.yeahboot.mq;

public interface MqListener {

    /**
     * 收到消息时回调
     */
    void onMessage(MqMessage message);

    /**
     * 获取监听的 topic / 队列
     */
    String getTopic();
}
