package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationInfoVo;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationVo;
import com.yeshimin.yeahboot.data.mapper.CsConversationMapper;
import com.yeshimin.yeahboot.data.repository.CsConversationRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.data.domain.dto.CsConversationQueryDto;
import com.yeshimin.yeahboot.merchant.service.MchCsConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端客服会话表
 */
@RestController
@RequestMapping("/mch/csConversation")
public class MchCsConversationController extends ShopCrudController<CsConversationMapper, CsConversationEntity, CsConversationRepo> {

    @Autowired
    private MchCsConversationService service;

    public MchCsConversationController(CsConversationRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:csConversation").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 查询会话列表
     */
    @GetMapping("/query")
    public R<Page<CsConversationVo>> query(Page<CsConversationVo> page, CsConversationQueryDto query) {
        Long userId = super.getUserId();
        return R.ok(service.query(userId, page, query));
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
     * 初始化会话
     */
    @PostMapping("/init")
    public R<CsConversationInfoVo> initConversation(@Validated @RequestBody IdDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.initConversation(userId, dto.getId()));
    }
}
