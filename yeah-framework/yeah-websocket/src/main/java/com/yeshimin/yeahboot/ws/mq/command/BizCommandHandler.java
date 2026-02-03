package com.yeshimin.yeahboot.ws.mq.command;

import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;

public interface BizCommandHandler {

    default void handle(BaseCommand command, SessionKey sk) {
    }

    String getCommand();
}
