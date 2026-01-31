package com.yeshimin.yeahboot.ws.websocket.handler;

import java.io.IOException;

public interface BaseWebSocketHandler {

    void sendMessageSingle(String userId, String message) throws IOException;

    String getNamespace();
}
