package com.yeshimin.yeahboot.app.ws.handler;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.service.CsChatService;
import com.yeshimin.yeahboot.ws.command.EchoCommand;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import com.yeshimin.yeahboot.ws.mq.command.BizCommandHandler;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import com.yeshimin.yeahboot.ws.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * APP端客服聊天业务处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppCsChatBizHandler implements BizCommandHandler {

    private final WebSocketService wsService;
    private final CsChatService csChatService;

    @Override
    public String getCommand() {
        return "cs-chat.mem";
    }

    @Override
    public void handle(BaseCommand baseCommand, SessionKey sk) {
        // 检查：subject是否正确
        if (!Objects.equals(sk.getSubject(), AuthSubjectEnum.APP.getValue())) {
            wsService.sendMessageToSession("subject参数错误", sk);
            return;
        }

        String subCmd = baseCommand.getSubCmd();

        switch (subCmd) {
            // for test
            case "echo": {
                EchoCommand echoCommand = EchoCommand.parse(baseCommand);
                wsService.sendMessageToSession(JSON.toJSONString(echoCommand), sk);
                break;
            }
            case "msg.mem2mch": {
                try {
                    csChatService.handleMsgMem2Mch(baseCommand, sk.getUserId());
                } catch (Exception e) {
                    log.error("handleMsgMem2Mch error: {}", e.getMessage(), e);
                    wsService.sendMessageToSession(e.getMessage(), sk);
                }
                break;
            }
            default:
                log.warn("unknown subCmd: {}", subCmd);
                break;
        }
    }
}
