package com.yeshimin.yeahboot.ws.websocket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultWebSocketHandler extends TextWebSocketHandler implements BaseWebSocketHandler {

    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("afterConnectionEstablished(), session: {}", session);

        String userId = (String) session.getAttributes().get("userId");
        SESSIONS.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("afterConnectionClosed(), session: {}, status: {}", session, status);

        String userId = (String) session.getAttributes().get("userId");
        SESSIONS.remove(userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("handleTextMessage(), message: {}", new String(message.asBytes()));

        WebSocketMessage<String> webSocketMessage = new TextMessage("hello: " + new String(message.asBytes()));
        session.sendMessage(webSocketMessage);
    }

    @Override
    public void sendMessageSingle(String userId, String message) throws IOException {
        log.info("sendMessageSingle(), userId: {}, message: {}", userId, message);

        WebSocketSession session = SESSIONS.get(userId);
        if (session == null) {
            throw new IllegalArgumentException();
        }

        session.sendMessage(new TextMessage(message));
    }

    @Override
    public String getNamespace() {
        return "default";
    }
}
