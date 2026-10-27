package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.MemberPointsLedgerVo;
import com.yeshimin.yeahboot.app.domain.vo.MemberPointsOverviewVo;
import com.yeshimin.yeahboot.app.domain.vo.MemberPointsRuleVo;
import com.yeshimin.yeahboot.app.service.AppMemberPointsService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsLedgerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App会员积分
 */
@RestController
@RequestMapping("/app/memberPoints")
@RequiredArgsConstructor
public class AppMemberPointsController extends BaseController {

    private final AppMemberPointsService service;

    /**
     * 查询积分概览
     */
    @GetMapping("/overview")
    public R<MemberPointsOverviewVo> overview() {
        return R.ok(service.overview(getUserId()));
    }

    /**
     * 查询积分流水
     */
    @GetMapping("/ledger")
    public R<IPage<MemberPointsLedgerVo>> ledger(Page<MemberPointsLedgerEntity> page) {
        return R.ok(service.ledger(getUserId(), page));
    }

    /**
     * 查询积分规则
     */
    @GetMapping("/rules")
    public R<List<MemberPointsRuleVo>> rules() {
        return R.ok(service.rules());
    }
}
