package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.mapper.CsMessageMapper;
import com.yeshimin.yeahboot.data.repository.CsMessageRepo;
import com.yeshimin.yeahboot.merchant.service.CsMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服消息表
 */
@RestController
@RequestMapping("/csMessage")
public class CsMessageController extends CrudController<CsMessageMapper, CsMessageEntity, CsMessageRepo> {

    @Autowired
    private CsMessageService service;

    public CsMessageController(CsMessageRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
