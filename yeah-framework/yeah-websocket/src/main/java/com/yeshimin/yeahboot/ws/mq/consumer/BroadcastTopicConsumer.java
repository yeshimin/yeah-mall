package com.yeshimin.yeahboot.ws.mq.consumer;

import com.yeshimin.yeahboot.ws.mq.TopicConst;
import com.yeshimin.yeahboot.ws.mq.TopicConsumer;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import com.yeshimin.yeahboot.ws.websocket.holder.DefaultSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;

/**
 * 广播消息主题消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BroadcastTopicConsumer implements TopicConsumer {

    private final DefaultSessionHolder sessionHolder;

    @Override
    public void consume(String topic, String message, SessionKey sk) {
        log.info("BroadcastTopicConsumer...topic: {}, sk: {}", topic, sk);

        Collection<WebSocketSession> sessions = sessionHolder.getSubjectSessions(sk.getSubject());
        if (sessions == null || sessions.isEmpty()) {
            log.error("sessions is null or empty, sk: {}", sk);
            return;
        }

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                log.error("session is null or not open, sk: {}", sessionHolder.getSk(session));
                continue;
            }

            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("send message error, sk: {}, message: {}", sessionHolder.getSk(session), message, e);
            }
        }
    }

    @Override
    public String getTopic() {
        return TopicConst.BROADCAST;
    }
}
