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

/**
 * 发送消息给会话维度主题消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendMessageToSessionTopicConsumer implements TopicConsumer {

    private final DefaultSessionHolder sessionHolder;

    @Override
    public void consume(String topic, String message, SessionKey sk) {
        log.info("SendMessageToSessionTopicConsumer...topic: {}, sk: {}", topic, sk);

        WebSocketSession session = sessionHolder.get(sk);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("send message error, sk: {}, message: {}", sk, message, e);
            }
        }
    }

    @Override
    public String getTopic() {
        return TopicConst.SEND_MSG_TO_SESSION;
    }
}
