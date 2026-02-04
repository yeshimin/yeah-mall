package com.yeshimin.yeahboot.app.ws.handler;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.ws.command.EchoCommand;
import com.yeshimin.yeahboot.app.ws.command.MsgCommand;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.data.common.enums.CsMsgDirectionEnum;
import com.yeshimin.yeahboot.data.common.enums.CsMsgTypeEnum;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.CsConversationRepo;
import com.yeshimin.yeahboot.data.repository.CsMessageRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.service.ValidateService;
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
    private final ValidateService validateService;

    private final ShopRepo shopRepo;
    private final CsConversationRepo csConversationRepo;
    private final CsMessageRepo csMessageRepo;

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
                MsgCommand command = MsgCommand.parse(baseCommand);
                MsgCommand.Payload payload = command.getPayload();
                // 检查参数
                if (validateService.hasError(payload)) {
                    wsService.sendMessageToSession("参数不正确", sk);
                    break;
                }
                Long shopId = payload.getShopId();
                Long mchId = payload.getFrom();
                Long memberId = payload.getTo();

                // 检查：shopId是否正确
                ShopEntity shop = shopRepo.findOneById(shopId);
                if (shop == null) {
                    wsService.sendMessageToSession("shopId参数错误", sk);
                    break;
                }
                // 检查：memberId是否正确
                if (!Objects.equals(sk.getUserId(), String.valueOf(memberId))) {
                    wsService.sendMessageToSession("from参数错误", sk);
                    break;
                }
                // 检查：mchId是否正确
                if (!Objects.equals(shop.getMchId(), mchId)) {
                    wsService.sendMessageToSession("to参数错误", sk);
                    break;
                }
                // 检查：type是否正确
                CsMsgTypeEnum msgType = CsMsgTypeEnum.of(String.valueOf(payload.getType()));
                if (msgType == null) {
                    wsService.sendMessageToSession("type参数错误", sk);
                    break;
                }
                // 获取会话，如果不存在则创建
                CsConversationEntity conversation = csConversationRepo.getOrCreateOne(mchId, shopId, memberId);
                // 创建消息
                csMessageRepo.createOne(conversation, CsMsgDirectionEnum.MEM2MCH.getIntValue(),
                        payload.getFrom(), payload.getTo(), payload.getContent(), payload.getType());
                // 发送给商家
                wsService.sendMessageToUser(JSON.toJSONString(command),
                        AuthSubjectEnum.MERCHANT.getValue(), String.valueOf(payload.getTo()));
                break;
            }
            default:
                log.warn("unknown subCmd: {}", subCmd);
                break;
        }
    }
}
