package com.yeshimin.yeahboot.merchant.ws.handler;

import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.service.CsChatService;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import com.yeshimin.yeahboot.ws.mq.command.BizCommandHandler;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import com.yeshimin.yeahboot.ws.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 商家端客服聊天业务处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MchCsChatBizHandler implements BizCommandHandler {

    private final WebSocketService wsService;
    private final CsChatService csChatService;

    @Override
    public String getCommand() {
        return "cs-chat.mch";
    }

    @Override
    public void handle(BaseCommand baseCommand, SessionKey sk) {
        // 检查：subject是否正确
        if (!Objects.equals(sk.getSubject(), AuthSubjectEnum.MERCHANT.getValue())) {
            wsService.sendMessageToSession("subject参数错误", sk);
            return;
        }

        String subCmd = baseCommand.getSubCmd();

        switch (subCmd) {
            case "msg.mch2mem": {
                try {
                    csChatService.handleMsgMch2Mem(baseCommand, sk.getUserId());
                } catch (Exception e) {
                    log.error("handleMsgMch2Mem error: {}", e.getMessage(), e);
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
