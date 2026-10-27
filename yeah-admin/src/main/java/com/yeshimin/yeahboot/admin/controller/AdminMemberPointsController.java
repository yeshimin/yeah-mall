package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.MemberPointsAdjustDto;
import com.yeshimin.yeahboot.admin.domain.dto.MemberPointsRuleUpdateDto;
import com.yeshimin.yeahboot.admin.service.AdminMemberPointsService;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsLedgerEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsRuleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台会员积分管理
 */
@RestController
@RequestMapping("/admin/memberPoints")
@RequiredArgsConstructor
public class AdminMemberPointsController {

    private final AdminMemberPointsService service;

    /**
     * 平台调整会员积分
     */
    @PostMapping("/account/adjust")
    public R<Void> adjustPoints(@Validated @RequestBody MemberPointsAdjustDto dto) {
        service.adjustPoints(dto);
        return R.ok();
    }

    /**
     * 查询积分流水
     */
    @GetMapping("/ledger/query")
    public R<IPage<MemberPointsLedgerEntity>> queryLedger(Page<MemberPointsLedgerEntity> page,
                                                           @RequestParam(required = false) Long memberId,
                                                           @RequestParam(required = false) String bizType) {
        return R.ok(service.queryLedger(page, memberId, bizType));
    }

    /**
     * 查询积分规则
     */
    @GetMapping("/rule/query")
    public R<List<MemberPointsRuleEntity>> queryRules() {
        return R.ok(service.queryRules());
    }

    /**
     * 更新积分规则
     */
    @PostMapping("/rule/update")
    public R<Void> updateRule(@Validated @RequestBody MemberPointsRuleUpdateDto dto) {
        service.updateRule(dto);
        return R.ok();
    }
}
