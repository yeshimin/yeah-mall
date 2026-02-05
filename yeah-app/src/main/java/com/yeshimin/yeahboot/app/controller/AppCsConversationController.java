package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.ConversationInitDto;
import com.yeshimin.yeahboot.app.domain.vo.CsConversationVo;
import com.yeshimin.yeahboot.app.service.AppCsConversationService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * APP端客服会话表
 */
@RestController
@RequestMapping("/app/csConversation")
@RequiredArgsConstructor
public class AppCsConversationController extends BaseController {

    private final AppCsConversationService service;

    // ================================================================================

    /**
     * 初始化会话
     * 如果已存在则返回已存在的会话
     */
    @RequestMapping("/init")
    public R<CsConversationVo> initConversation(@Validated @RequestBody ConversationInitDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.initConversation(userId, dto.getConversationId(), dto.getShopId()));
    }

    /**
     * 查询会话列表
     */
    @RequestMapping("/query")
    public R<Page<CsConversationEntity>> query(Page<CsConversationEntity> page) {
        Long userId = super.getUserId();
        return R.ok(service.query(userId, page));
    }

    /**
     * 查询会话消息
     */
    @RequestMapping("/messages")
    public R<Page<CsMessageEntity>> queryMessages(Page<CsMessageEntity> page, @RequestParam Long conversationId) {
        Long userId = super.getUserId();
        return R.ok(service.queryMessages(userId, page, conversationId));
    }
}
