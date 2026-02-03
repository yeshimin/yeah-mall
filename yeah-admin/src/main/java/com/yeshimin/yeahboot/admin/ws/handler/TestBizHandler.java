package com.yeshimin.yeahboot.admin.ws.handler;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.admin.ws.command.EchoCommand;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import com.yeshimin.yeahboot.ws.mq.command.BizCommandHandler;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import com.yeshimin.yeahboot.ws.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试业务处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestBizHandler implements BizCommandHandler {

    private final WebSocketService wsService;

    @Override
    public String getCommand() {
        return "test";
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
