package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.merchant.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.MemberMapper;
import com.yeshimin.yeahboot.merchant.data.repository.MemberRepo;
import com.yeshimin.yeahboot.admin.service.MemberAdminService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员表
 */
@RestController
@RequestMapping("/member")
public class MemberAdminController extends CrudController<MemberMapper, MemberEntity, MemberRepo> {

    @Autowired
    private MemberAdminService service;

    public MemberAdminController(MemberRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
