package com.yeshimin.yeahboot.ws.controller;

import com.yeshimin.yeahboot.ws.websocket.handler.BaseWebSocketHandler;
import com.yeshimin.yeahboot.ws.websocket.handler.DefaultWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/ws/test")
@AllArgsConstructor
public class TestController {

    private final DefaultWebSocketHandler defaultWebSocketHandler;

    private static final Map<String, BaseWebSocketHandler> HANDLERS = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        HANDLERS.put(defaultWebSocketHandler.getNamespace(), defaultWebSocketHandler);
    }

    @PostMapping("/sendMessageSingle")
    public void sendMessageSingle(@RequestParam("namespace") String namespace,
                                  @RequestParam("userId") String userId,
                                  @RequestParam("message") String message) throws IOException {
        this.getHandler(namespace).sendMessageSingle(userId, message);
    }

    // TODO broadcast

    private BaseWebSocketHandler getHandler(String namespace) {
        return HANDLERS.getOrDefault(namespace, defaultWebSocketHandler);
    }
}
