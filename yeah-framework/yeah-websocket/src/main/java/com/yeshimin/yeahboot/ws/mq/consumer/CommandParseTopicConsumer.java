package com.yeshimin.yeahboot.ws.mq.consumer;

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

/**
 * 指令解析主题消费者
 */
@Slf4j
@Component
public class CommandParseTopicConsumer implements TopicConsumer {

    @Lazy
    @Autowired
    private MqService mqService;

    @Override
    public void consume(String topic, String message, SessionKey sk) {
        log.info("CommandParseTopicConsumer...topic: {}, sk: {}", topic, sk);

        BaseCommand command = JSON.parseObject(message, BaseCommand.class);
        if (command == null || command.getCategory() == null || command.getCategory().trim().isEmpty()) {
            log.warn("command...command unknown");
            return;
        }

        switch (command.getCategory()) {
            case "heartbeat":
                mqService.publish(TopicConst.HEARTBEAT, message, sk);
                break;
            case "biz-handle":
                mqService.publish(TopicConst.BIZ_HANDLE, message, sk);
                break;
            default:
                log.warn("command...command unknown");
                break;
        }
    }

    @Override
    public String getTopic() {
        return TopicConst.COMMAND_PARSE;
    }
}
