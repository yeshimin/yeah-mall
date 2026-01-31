package com.yeshimin.yeahboot.ws.websocket.interceptor;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO 可以做鉴权？！；获取客户端（用户）标识
 */
@Slf4j
public class MyHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("beforeHandshake(), request: {}, response: {}, wsHandler: {}, attributes: {}",
                request, response, wsHandler, attributes);

        String query = request.getURI().getQuery();
        String userId = this.parseUserId(query);
        log.info("userId: {}", userId);

        if (StrUtil.isBlank(userId)) {
            return false;
        } else {
            attributes.put("userId", userId);
            return true;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("afterHandshake(), request: {}, response: {}, wsHandler: {}, exception: {}",
                request, response, wsHandler, exception);
    }

    private String parseUserId(String query) {
        if (StrUtil.isBlank(query)) {
            return null;
        }

        return Arrays.stream(query.split("&"))
                .filter(e -> e.contains("userId="))
                .map(e -> e.substring(e.indexOf("=") + 1))
                .collect(Collectors.joining());
    }
}
