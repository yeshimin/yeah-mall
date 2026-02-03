package com.yeshimin.yeahboot.ws.websocket.handler;

import com.yeshimin.yeahboot.ws.mq.MqService;
import com.yeshimin.yeahboot.ws.mq.TopicConst;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import com.yeshimin.yeahboot.ws.websocket.holder.DefaultSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultWebSocketHandler extends TextWebSocketHandler implements BaseWebSocketHandler {

    private final DefaultSessionHolder defaultSessionHolder;

    private final MqService mqService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("afterConnectionEstablished(), session: {}", session);

        SessionKey sk = (SessionKey) session.getAttributes().get("sk");
        WebSocketSession oldSession = defaultSessionHolder.put(sk, session);
        // 清除旧的会话
        if (oldSession != null && oldSession.isOpen()) {
            try {
                oldSession.close();
            } catch (IOException e) {
                log.error("afterConnectionClosed(), oldSession close error", e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("afterConnectionClosed(), session: {}, status: {}", session, status);

        SessionKey sk = (SessionKey) session.getAttributes().get("sk");
        WebSocketSession existSession = defaultSessionHolder.get(sk);
        if (existSession != null && Objects.equals(existSession.getId(), session.getId())) {
            defaultSessionHolder.remove(sk);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("handleTransportError(), session: {}", session, exception);

        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException e) {
                log.error("handleTransportError(), session close error", e);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("handleTextMessage(), message: {}", new String(message.asBytes()));

        SessionKey sk = (SessionKey) session.getAttributes().get("sk");

        // 进行消息解析
        mqService.publish(TopicConst.COMMAND_PARSE, message.getPayload(), sk);
    }

    // --------------------------------------------------------------------------------

    @Override
    public String getNamespace() {
        return "default";
    }
}
