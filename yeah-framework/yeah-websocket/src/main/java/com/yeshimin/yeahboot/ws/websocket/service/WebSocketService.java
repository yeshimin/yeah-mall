package com.yeshimin.yeahboot.ws.websocket.service;

import com.yeshimin.yeahboot.ws.mq.MqService;
import com.yeshimin.yeahboot.ws.mq.TopicConst;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebSocketService {

    @Lazy
    @Autowired
    private MqService mqService;

    /**
     * 发送单个给单个用户
     * 一个用户可能有多个端的会话
     * bizType暂时只匹配default
     */
    public void sendMessageToUser(String message, String subject, String userId) {
        log.info("sendMessageToUser()...subject: {}, userId: {}", subject, userId);
        if (message == null || subject == null || userId == null) {
            throw new IllegalArgumentException("message, subject or userId is null");
        }
        mqService.publish(TopicConst.SEND_MSG_TO_USER, message, SessionKey.ofUser(subject, userId));
    }

    /**
     * 发送单个给单个会话
     * bizType暂时只匹配default
     */
    public void sendMessageToSession(String message, SessionKey sk) {
        log.info("sendMessageToSession()...sk: {}", sk);
        if (message == null || sk == null) {
            throw new IllegalArgumentException("message or SessionKey is null");
        }
        mqService.publish(TopicConst.SEND_MSG_TO_SESSION, message, sk);
    }

    /**
     * 发送广播消息
     * 所有该业务系统下的用户都会收到
     * 一个用户可能有多个端的会话
     * bizType暂时只匹配default
     */
    public void sendMessageBroadcast(String message, String subject) {
        log.info("sendMessageBroadcast()...subject: {}", subject);
        if (message == null || subject == null) {
            throw new IllegalArgumentException("message or subject is null");
        }
        mqService.publish(TopicConst.BROADCAST, message, SessionKey.ofSubject(subject));
    }
}
