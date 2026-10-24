package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.ConversationInitDto;
import com.yeshimin.yeahboot.app.service.AppCsConversationService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationInfoVo;
import com.yeshimin.yeahboot.service.CsChatService;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/csConversation")
@RequiredArgsConstructor
public class AppCsConversationController extends BaseController {

    private final AppCsConversationService service;
    private final CsChatService csChatService;

    // ================================================================================

    /**
     * 初始化会话
     * 如果已存在则返回已存在的会话
     */
    @PostMapping("/init")
    public R<CsConversationInfoVo> initConversation(@Validated @RequestBody ConversationInitDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.initConversation(userId, dto.getConversationId(), dto.getShopId()));
    }

    /**
     * 查询会话列表
     */
    @GetMapping("/query")
    public R<Page<CsConversationEntity>> query(Page<CsConversationEntity> page) {
        Long userId = super.getUserId();
        return R.ok(service.query(userId, page));
    }

    /**
     * 查询会话消息
     */
    @GetMapping("/messages")
    public R<Page<CsMessageEntity>> queryMessages(Page<CsMessageEntity> page, @RequestParam Long conversationId) {
        Long userId = super.getUserId();
        return R.ok(service.queryMessages(userId, page, conversationId));
    }

    /**
     * 发送买家客服消息
     */
    @PostMapping("/sendMessage")
    public R<Void> sendMessage(@RequestBody BaseCommand command) {
        // HTTP写入复用现有WebSocket业务处理，保证鉴权、落库和推送行为一致。
        csChatService.handleMsgMem2Mch(command, String.valueOf(super.getUserId()));
        return R.ok();
    }
}
