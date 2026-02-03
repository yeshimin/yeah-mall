package com.yeshimin.yeahboot.ws.heartbeat;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.ws.mq.MqService;
import com.yeshimin.yeahboot.ws.mq.TopicConst;
import com.yeshimin.yeahboot.ws.mq.TopicConsumer;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 心跳主题消费者
 */
@Slf4j
@Component
public class HeartbeatTopicConsumer implements TopicConsumer {

    @Lazy
    @Autowired
    private MqService mqService;

    @Override
    public void consume(String topic, String message, SessionKey sk) {
        log.info("HeartbeatTopicConsumer...topic: {}, sk: {}", topic, sk);

        BaseCommand command = JSON.parseObject(message, BaseCommand.class);

        // 如果客户端发送ping，则回复pong
        if (Objects.equals(command.getCommand(), "ping")) {
            // 更新心跳
            sk.setLastHeartbeat(System.currentTimeMillis() / 1000);

            String reply = JSON.toJSONString(new PongCommand());
            mqService.publish(TopicConst.SEND_MSG_TO_SESSION, reply, sk);
        }
    }

    @Override
    public String getTopic() {
        return TopicConst.HEARTBEAT;
    }
}
