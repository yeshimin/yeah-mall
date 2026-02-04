package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.mapper.CsConversationMapper;
import com.yeshimin.yeahboot.data.repository.CsConversationRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.CsConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服会话表
 */
@RestController
@RequestMapping("/mch/csConversation")
public class CsConversationController extends ShopCrudController<CsConversationMapper, CsConversationEntity, CsConversationRepo> {

    @Autowired
    private CsConversationService service;

    public CsConversationController(CsConversationRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:csConversation").disableCreate().disableUpdate();
    }

    // ================================================================================

    /**
     * 查询会话消息
     */
    @RequestMapping("/messages")
    public R<Page<CsMessageEntity>> queryMessages(Page<CsMessageEntity> page, @RequestParam Long conversationId) {
        Long userId = super.getUserId();
        return R.ok(service.queryMessages(userId, page, conversationId));
    }
}
