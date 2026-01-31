package com.yeshimin.yeahboot.ws.config;

import com.yeshimin.yeahboot.ws.websocket.handler.DefaultWebSocketHandler;
import com.yeshimin.yeahboot.ws.websocket.interceptor.MyHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@EnableWebSocket
@Configuration
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final DefaultWebSocketHandler defaultWebSocketHandler;

    /**
     * ns = namespace
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(defaultWebSocketHandler, "/ns/default")
                .addInterceptors(new MyHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
