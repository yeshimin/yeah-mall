package com.yeshimin.yeahboot.service;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.CsMsgDirectionEnum;
import com.yeshimin.yeahboot.data.common.enums.CsMsgTypeEnum;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.repository.CsConversationRepo;
import com.yeshimin.yeahboot.data.repository.CsMessageRepo;
import com.yeshimin.yeahboot.storage.StorageManager;
import com.yeshimin.yeahboot.ws.command.MsgCommand;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 客服聊天服务，多端共用
 */
@Service
@RequiredArgsConstructor
public class CsChatService {

    private final CsConversationRepo csConversationRepo;
    private final CsMessageRepo csMessageRepo;
    private final CsRealtimeService realtimeService;
    private final ValidateService validateService;
    private final StorageManager storageManager;

    /**
     * 处理买家发送给商家的消息
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMsgMem2Mch(BaseCommand baseCommand, String userId) {
        MsgCommand command = MsgCommand.parse(baseCommand);
        MsgCommand.Payload payload = command.getPayload();
        // 参数和消息类型必须在持久化之前完成校验。
        if (validateService.hasError(payload)) {
            throw new BaseException("参数错误");
        }
        CsMsgTypeEnum msgType = CsMsgTypeEnum.of(String.valueOf(payload.getType()));
        if (msgType == null) {
            throw new BaseException("type参数错误");
        }

        // 登录买家只能向自己的会话发送消息。
        CsConversationEntity conversation = csConversationRepo.getOneById(payload.getConversationId(), "会话不存在");
        if (!Objects.equals(userId, String.valueOf(conversation.getMemberId()))) {
            throw new BaseException("会话权限错误");
        }

        if (msgType == CsMsgTypeEnum.IMAGE) {
            storageManager.markUse(payload.getContent());
        }
        CsMessageEntity message = csMessageRepo.createOne(
                conversation,
                CsMsgDirectionEnum.MEM2MCH.getIntValue(),
                conversation.getMemberId(),
                conversation.getMchId(),
                payload.getContent(),
                payload.getType());
        csConversationRepo.updateLastMessage(
                conversation, this.getLastMessage(payload.getContent(), msgType), message.getCreateTime());

        // 事件路由由服务端固定，避免客户端构造无法识别或错误方向的推送命令。
        this.normalizeCommand(command, "cs-chat.mem", "msg.mem2mch");
        realtimeService.publishAfterCommit(
                JSON.toJSONString(command),
                AuthSubjectEnum.MERCHANT.getValue(),
                String.valueOf(conversation.getMchId()));
    }

    /**
     * 处理商家发送给买家的消息
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMsgMch2Mem(BaseCommand baseCommand, String userId) {
        MsgCommand command = MsgCommand.parse(baseCommand);
        MsgCommand.Payload payload = command.getPayload();
        // 参数和消息类型必须在持久化之前完成校验。
        if (validateService.hasError(payload)) {
            throw new BaseException("参数错误");
        }
        CsMsgTypeEnum msgType = CsMsgTypeEnum.of(String.valueOf(payload.getType()));
        if (msgType == null) {
            throw new BaseException("type参数错误");
        }

        // 登录商家只能向本商家的会话发送消息。
        CsConversationEntity conversation = csConversationRepo.getOneById(payload.getConversationId(), "会话不存在");
        if (!Objects.equals(userId, String.valueOf(conversation.getMchId()))) {
            throw new BaseException("会话权限错误");
        }

        if (msgType == CsMsgTypeEnum.IMAGE) {
            storageManager.markUse(payload.getContent());
        }
        CsMessageEntity message = csMessageRepo.createOne(
                conversation,
                CsMsgDirectionEnum.MCH2MEM.getIntValue(),
                conversation.getMchId(),
                conversation.getMemberId(),
                payload.getContent(),
                payload.getType());
        csConversationRepo.updateLastMessage(
                conversation, this.getLastMessage(payload.getContent(), msgType), message.getCreateTime());

        // 事件路由由服务端固定，避免客户端构造无法识别或错误方向的推送命令。
        this.normalizeCommand(command, "cs-chat.mch", "msg.mch2mem");
        realtimeService.publishAfterCommit(
                JSON.toJSONString(command),
                AuthSubjectEnum.APP.getValue(),
                String.valueOf(conversation.getMemberId()));
    }

    private void normalizeCommand(MsgCommand command, String commandName, String subCommand) {
        command.setCategory("biz-handle");
        command.setCommand(commandName);
        command.setSubCmd(subCommand);
    }

    private String getLastMessage(String msgContent, CsMsgTypeEnum msgType) {
        return msgType == CsMsgTypeEnum.TEXT ? msgContent : "[图片]";
    }
}
