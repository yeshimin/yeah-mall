package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.MemberPointsLedgerVo;
import com.yeshimin.yeahboot.app.domain.vo.MemberPointsOverviewVo;
import com.yeshimin.yeahboot.app.domain.vo.MemberPointsRuleVo;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsAccountEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsLedgerEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsRuleEntity;
import com.yeshimin.yeahboot.data.repository.MemberPointsAccountRepo;
import com.yeshimin.yeahboot.data.repository.MemberPointsLedgerRepo;
import com.yeshimin.yeahboot.data.repository.MemberPointsRuleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * App会员积分服务
 */
@Service
@RequiredArgsConstructor
public class AppMemberPointsService {

    private final MemberPointsAccountRepo accountRepo;
    private final MemberPointsLedgerRepo ledgerRepo;
    private final MemberPointsRuleRepo ruleRepo;

    /**
     * 查询积分概览
     */
    public MemberPointsOverviewVo overview(Long memberId) {
        // 积分账户采用首次变动时创建，未开户会员对App统一呈现为零余额。
        MemberPointsAccountEntity account = accountRepo.lambdaQuery()
                .eq(MemberPointsAccountEntity::getMemberId, memberId).one();
        MemberPointsOverviewVo result = new MemberPointsOverviewVo();
        result.setBalance(account == null ? 0L : account.getBalance());
        result.setTotalEarned(account == null ? 0L : account.getTotalEarned());
        result.setTotalUsed(account == null ? 0L : account.getTotalUsed());
        return result;
    }

    /**
     * 查询积分流水
     */
    public IPage<MemberPointsLedgerVo> ledger(Long memberId, Page<MemberPointsLedgerEntity> page) {
        // 流水只允许查询当前会员自己的记录，避免通过分页参数越权查看其他会员资产。
        Page<MemberPointsLedgerEntity> result = ledgerRepo.lambdaQuery()
                .eq(MemberPointsLedgerEntity::getMemberId, memberId)
                .orderByDesc(MemberPointsLedgerEntity::getCreateTime)
                .page(page);
        return result.convert(entity -> BeanUtil.copyProperties(entity, MemberPointsLedgerVo.class));
    }

    /**
     * 查询积分规则
     */
    public List<MemberPointsRuleVo> rules() {
        // App 仅展示当前启用规则，已停用规则不再对外承诺积分权益。
        return ruleRepo.lambdaQuery().eq(MemberPointsRuleEntity::getIsEnabled, true)
                .orderByAsc(MemberPointsRuleEntity::getSort).list().stream()
                .map(entity -> BeanUtil.copyProperties(entity, MemberPointsRuleVo.class))
                .collect(Collectors.toList());
    }
}
