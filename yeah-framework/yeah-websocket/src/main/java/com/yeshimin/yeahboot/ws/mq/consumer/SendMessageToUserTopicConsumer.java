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
 * 发送消息给用户维度主题消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendMessageToUserTopicConsumer implements TopicConsumer {

    private final DefaultSessionHolder sessionHolder;

    @Override
    public void consume(String topic, String message, SessionKey sk) {
        log.info("SendMessageToUserTopicConsumer...topic: {}, sk: {}", topic, sk);

        Collection<WebSocketSession> sessions = sessionHolder.getUserSessions(sk.getSubject(), sk.getUserId());
        if (sessions == null || sessions.isEmpty()) {
            log.error("sessions is null or empty, sk: {}", sk);
            return;
        }

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                log.error("session is null or not open, sk: {}", sk);
                continue;
            }

            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("send message error, sk: {}, message: {}", sk, message, e);
            }
        }
    }

    @Override
    public String getTopic() {
        return TopicConst.SEND_MSG_TO_USER;
    }
}
