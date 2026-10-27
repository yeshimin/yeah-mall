package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.mapper.MemberMapper;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.data.domain.vo.AdminMemberVo;
import com.yeshimin.yeahboot.admin.service.AdminMemberService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员表
 */
@RestController
@RequestMapping("/admin/member")
public class AdminMemberController extends CrudController<MemberMapper, MemberEntity, MemberRepo> {

    @Autowired
    private AdminMemberService service;

    public AdminMemberController(MemberRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================

    /**
     * 分页查询会员管理列表
     */
    @GetMapping("/query")
    public R<IPage<AdminMemberVo>> query(Page<AdminMemberVo> page,
                                         @RequestParam(required = false) Long memberId,
                                         @RequestParam(required = false) String keyword) {
        return R.ok(service.query(page, memberId, keyword));
    }
}
