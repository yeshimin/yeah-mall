package com.yeshimin.yeahboot.app.ws.handler;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.ws.command.EchoCommand;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import com.yeshimin.yeahboot.ws.mq.command.BizCommandHandler;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import com.yeshimin.yeahboot.ws.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * APP端客服聊天业务处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppCsChatBizHandler implements BizCommandHandler {

    private final WebSocketService wsService;

    @Override
    public String getCommand() {
        return "cs-chat";
    }

    @Override
    public void handle(BaseCommand command, SessionKey sk) {
        String subCmd = command.getSubCmd();

        switch (subCmd) {
            case "echo": {
                EchoCommand echoCommand = EchoCommand.parse(command);
                wsService.sendMessageToSession(JSON.toJSONString(echoCommand), sk);
                break;
            }
            default:
                log.warn("unknown subCmd: {}", subCmd);
                break;
        }
    }
}
