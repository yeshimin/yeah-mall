package com.yeshimin.yeahboot.ws.websocket.interceptor;

import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
public class DefaultHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("beforeHandshake(), request: {}, response: {}, wsHandler: {}, attributes: {}",
                request, response, wsHandler, attributes);

        String userId = String.valueOf(WebContextUtils.getUserId());
        String subject = WebContextUtils.getSubject();
        String terminal = WebContextUtils.getTerminal();
        log.info("subject: {}, userId: {}, terminal: {}", subject, userId, terminal);

        SessionKey sk = new SessionKey();
        sk.setUserId(userId);
        sk.setSubject(subject);
        sk.setTerminal(terminal);
        sk.setBizType("default");
        attributes.put("sk", sk);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("afterHandshake(), request: {}, response: {}, wsHandler: {}", request, response, wsHandler, exception);
    }
}
