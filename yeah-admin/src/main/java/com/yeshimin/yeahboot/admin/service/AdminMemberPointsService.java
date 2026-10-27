package com.yeshimin.yeahboot.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.MemberPointsAdjustDto;
import com.yeshimin.yeahboot.admin.domain.dto.MemberPointsRuleUpdateDto;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsLedgerEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsRuleEntity;
import com.yeshimin.yeahboot.data.repository.MemberPointsLedgerRepo;
import com.yeshimin.yeahboot.data.repository.MemberPointsRuleRepo;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.service.MemberPointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 平台会员积分服务
 */
@Service
@RequiredArgsConstructor
public class AdminMemberPointsService {

    private final MemberPointsLedgerRepo ledgerRepo;
    private final MemberPointsRuleRepo ruleRepo;
    private final MemberRepo memberRepo;
    private final MemberPointsService memberPointsService;

    /**
     * 平台调整会员积分
     */
    @Transactional(rollbackFor = Exception.class)
    public void adjustPoints(MemberPointsAdjustDto dto) {
        // 先校验会员存在，再由统一记账服务完成开户、余额锁定和流水落库。
        memberRepo.getOneById(dto.getMemberId(), "会员不存在");
        memberPointsService.manualAdjust(dto.getMemberId(), dto.getChangePoints(), dto.getRemark());
    }

    /**
     * 查询积分流水
     */
    public IPage<MemberPointsLedgerEntity> queryLedger(Page<MemberPointsLedgerEntity> page, Long memberId,
                                                        String bizType) {
        return ledgerRepo.lambdaQuery()
                .eq(memberId != null, MemberPointsLedgerEntity::getMemberId, memberId)
                .eq(bizType != null && !bizType.trim().isEmpty(), MemberPointsLedgerEntity::getBizType, bizType)
                .orderByDesc(MemberPointsLedgerEntity::getCreateTime).page(page);
    }

    /**
     * 查询积分规则
     */
    public List<MemberPointsRuleEntity> queryRules() {
        return ruleRepo.lambdaQuery().orderByAsc(MemberPointsRuleEntity::getSort).list();
    }

    /**
     * 更新积分规则
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(MemberPointsRuleUpdateDto dto) {
        MemberPointsRuleEntity rule = ruleRepo.getOneById(dto.getId(), "积分规则不存在");
        rule.setFixedPoints(dto.getFixedPoints());
        rule.setPointsPerYuan(dto.getPointsPerYuan());
        rule.setIsEnabled(dto.getIsEnabled());
        rule.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
        rule.updateById();
    }
}
