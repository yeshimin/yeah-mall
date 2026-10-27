package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.IdUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.MemberPointsBizTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.MemberPointsLedgerTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.MemberPointsRuleCalcTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.MemberPointsRuleCodeEnum;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsAccountEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsLedgerEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsRuleEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.repository.MemberPointsAccountRepo;
import com.yeshimin.yeahboot.data.repository.MemberPointsLedgerRepo;
import com.yeshimin.yeahboot.data.repository.MemberPointsRuleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 会员积分统一记账服务
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MemberPointsService {

    private final MemberPointsAccountRepo accountRepo;
    private final MemberPointsLedgerRepo ledgerRepo;
    private final MemberPointsRuleRepo ruleRepo;

    /**
     * 注册赠送积分
     */
    public void grantForRegister(Long memberId) {
        grant(memberId, MemberPointsRuleCodeEnum.REGISTER, BigDecimal.ZERO,
                MemberPointsBizTypeEnum.REGISTER, memberId, "", "注册赠送积分");
    }

    /**
     * 订单完成赠送积分
     */
    public void grantForOrderComplete(OrderEntity order) {
        grant(order.getMemberId(), MemberPointsRuleCodeEnum.ORDER_COMPLETE, order.getFinalAmount(),
                MemberPointsBizTypeEnum.ORDER_COMPLETE,
                order.getId(), order.getOrderNo(), "订单完成赠送积分");
    }

    /**
     * 完成评价赠送积分
     */
    public void grantForReview(OrderEntity order) {
        grant(order.getMemberId(), MemberPointsRuleCodeEnum.REVIEW, BigDecimal.ZERO,
                MemberPointsBizTypeEnum.REVIEW,
                order.getId(), order.getOrderNo(), "完成评价赠送积分");
    }

    /**
     * 退款成功回退订单完成积分
     */
    public void reverseForOrderRefund(OrderEntity order) {
        if (ledgerRepo.existsByBiz(MemberPointsBizTypeEnum.ORDER_REFUND.getValue(), order.getId())) {
            return;
        }
        // 仅回退已实际发放过的订单完成积分，避免退款回调重试重复扣减。
        MemberPointsLedgerEntity earned = ledgerRepo.findOneByBiz(
                MemberPointsBizTypeEnum.ORDER_COMPLETE.getValue(), order.getId());
        if (earned == null || earned.getChangePoints() <= 0) {
            return;
        }
        MemberPointsAccountEntity account = accountRepo.findByMemberIdForUpdate(order.getMemberId());
        if (account == null) {
            return;
        }
        // 退款后允许余额为负，确保已消费的奖励积分仍会被完整回退。
        long points = earned.getChangePoints();
        long balanceBefore = account.getBalance();
        long balanceAfter = balanceBefore - points;
        account.setBalance(balanceAfter);
        account.setTotalEarned(Math.max(0L, account.getTotalEarned() - points));
        account.updateById();

        MemberPointsLedgerEntity ledger = new MemberPointsLedgerEntity();
        ledger.setMemberId(order.getMemberId());
        ledger.setChangePoints(-points);
        ledger.setBalanceBefore(balanceBefore);
        ledger.setBalanceAfter(balanceAfter);
        ledger.setType(MemberPointsLedgerTypeEnum.ADJUST.getIntValue());
        ledger.setBizType(MemberPointsBizTypeEnum.ORDER_REFUND.getValue());
        ledger.setBizId(order.getId());
        ledger.setBizNo(order.getOrderNo());
        ledger.setRuleCode(MemberPointsRuleCodeEnum.ORDER_COMPLETE.getValue());
        ledger.setDescription("订单退款回退积分");
        ledger.insert();
    }

    /**
     * 锁定会员积分账户
     */
    public MemberPointsAccountEntity lockAccount(Long memberId) {
        MemberPointsAccountEntity account = accountRepo.findByMemberIdForUpdate(memberId);
        if (account != null) {
            return account;
        }

        // 首次积分变动时懒创建账户，历史会员无需预先批量开户。
        account = new MemberPointsAccountEntity();
        account.setMemberId(memberId);
        account.setBalance(0L);
        account.setTotalEarned(0L);
        account.setTotalUsed(0L);
        account.insert();
        return accountRepo.findByMemberIdForUpdate(memberId);
    }

    /**
     * 扣减兑换积分
     */
    public void consume(Long memberId, Long points, MemberPointsBizTypeEnum bizType,
                        Long bizId, String bizNo, String description) {
        if (points == null || points <= 0) {
            throw new BaseException("兑换积分必须大于0");
        }
        // 业务单号在积分流水中唯一，同一兑换请求重试不应重复扣积分。
        if (ledgerRepo.existsByBiz(bizType.getValue(), bizId)) {
            return;
        }
        MemberPointsAccountEntity account = lockAccount(memberId);
        if (account.getBalance() < points) {
            throw new BaseException("积分余额不足");
        }
        long before = account.getBalance();
        long after = before - points;
        account.setBalance(after);
        account.setTotalUsed(account.getTotalUsed() + points);
        account.updateById();
        createLedger(memberId, -points, before, after, MemberPointsLedgerTypeEnum.CONSUME,
                bizType, bizId, bizNo, null, description);
    }

    /**
     * 取消兑换退还积分
     */
    public void refundConsume(Long memberId, Long points, MemberPointsBizTypeEnum bizType,
                              Long bizId, String bizNo, String description) {
        // 取消补偿同样按业务单幂等，防止库存或消息重试导致重复返还。
        if (ledgerRepo.existsByBiz(bizType.getValue(), bizId)) {
            return;
        }
        // 锁定账户行后返还积分，避免与兑换扣减并发覆盖余额。
        MemberPointsAccountEntity account = lockAccount(memberId);
        long before = account.getBalance();
        long after = before + points;
        account.setBalance(after);
        account.setTotalUsed(Math.max(0L, account.getTotalUsed() - points));
        account.updateById();
        createLedger(memberId, points, before, after, MemberPointsLedgerTypeEnum.ADJUST,
                bizType, bizId, bizNo, null, description);
    }

    /**
     * 平台人工调整会员积分
     */
    public void manualAdjust(Long memberId, Long changePoints, String remark) {
        if (changePoints == null || changePoints == 0) {
            throw new BaseException("调整积分不能为0");
        }
        // 锁定账户后再计算正负调整，避免后台调整与用户兑换产生余额覆盖。
        MemberPointsAccountEntity account = lockAccount(memberId);
        long before = account.getBalance();
        long after;
        try {
            after = Math.addExact(before, changePoints);
        } catch (ArithmeticException exception) {
            throw new BaseException("调整后的积分超出允许范围");
        }
        if (after < 0) {
            throw new BaseException("可用积分不足，不能扣减");
        }

        long totalEarned = account.getTotalEarned();
        long totalUsed = account.getTotalUsed();
        try {
            if (changePoints > 0) {
                totalEarned = Math.addExact(totalEarned, changePoints);
            } else {
                totalUsed = Math.addExact(totalUsed, Math.negateExact(changePoints));
            }
        } catch (ArithmeticException exception) {
            throw new BaseException("累计积分超出允许范围");
        }
        account.setBalance(after);
        account.setTotalEarned(totalEarned);
        account.setTotalUsed(totalUsed);
        account.updateById();

        // 人工调整生成独立业务ID，保证同一会员可以产生多笔调整流水。
        long adjustId = IdUtil.getSnowflakeNextId();
        String description = remark == null || remark.trim().isEmpty()
                ? "平台调整积分"
                : "平台调整积分：" + remark.trim();
        createLedger(memberId, changePoints, before, after, MemberPointsLedgerTypeEnum.ADJUST,
                MemberPointsBizTypeEnum.MANUAL_ADJUST, adjustId, "ADJ" + adjustId, null, description);
    }

    /**
     * 按规则幂等发放积分
     */
    public void grant(Long memberId, MemberPointsRuleCodeEnum ruleCode, BigDecimal amount,
                      MemberPointsBizTypeEnum bizType,
                      Long bizId, String bizNo, String description) {
        // 注册、订单完成和评价奖励均按业务ID幂等发放。
        if (ledgerRepo.existsByBiz(bizType.getValue(), bizId)) {
            return;
        }
        // 规则被停用时不补发历史事件，当前业务仅在规则启用时产生积分。
        MemberPointsRuleEntity rule = ruleRepo.findEnabledByCode(ruleCode.getValue());
        if (rule == null) {
            return;
        }
        long points = calculatePoints(rule, amount);
        if (points <= 0) {
            return;
        }

        // 奖励与兑换共用同一账户锁，保证余额和累计获得积分同步递增。
        MemberPointsAccountEntity account = lockAccount(memberId);

        long balanceBefore = account.getBalance();
        long balanceAfter = balanceBefore + points;
        account.setBalance(balanceAfter);
        account.setTotalEarned(account.getTotalEarned() + points);
        account.updateById();

        // 账户余额与流水在同一事务落库，流水保存变动前后余额便于对账。
        MemberPointsLedgerEntity ledger = new MemberPointsLedgerEntity();
        ledger.setMemberId(memberId);
        ledger.setChangePoints(points);
        ledger.setBalanceBefore(balanceBefore);
        ledger.setBalanceAfter(balanceAfter);
        ledger.setType(MemberPointsLedgerTypeEnum.EARN.getIntValue());
        ledger.setBizType(bizType.getValue());
        ledger.setBizId(bizId);
        ledger.setBizNo(bizNo == null ? "" : bizNo);
        ledger.setRuleCode(ruleCode.getValue());
        ledger.setDescription(description);
        ledger.insert();
    }

    private void createLedger(Long memberId, long change, long before, long after,
                              MemberPointsLedgerTypeEnum type, MemberPointsBizTypeEnum bizType,
                              Long bizId, String bizNo, MemberPointsRuleCodeEnum ruleCode,
                              String description) {
        MemberPointsLedgerEntity ledger = new MemberPointsLedgerEntity();
        ledger.setMemberId(memberId);
        ledger.setChangePoints(change);
        ledger.setBalanceBefore(before);
        ledger.setBalanceAfter(after);
        ledger.setType(type.getIntValue());
        ledger.setBizType(bizType.getValue());
        ledger.setBizId(bizId);
        ledger.setBizNo(bizNo == null ? "" : bizNo);
        ledger.setRuleCode(ruleCode == null ? "" : ruleCode.getValue());
        ledger.setDescription(description);
        ledger.insert();
    }

    private long calculatePoints(MemberPointsRuleEntity rule, BigDecimal amount) {
        if (MemberPointsRuleCalcTypeEnum.FIXED.equalsValue(rule.getCalcType())) {
            return rule.getFixedPoints() == null ? 0L : rule.getFixedPoints();
        }
        if (MemberPointsRuleCalcTypeEnum.AMOUNT_RATIO.equalsValue(rule.getCalcType())
                && amount != null && rule.getPointsPerYuan() != null) {
            return amount.multiply(rule.getPointsPerYuan()).setScale(0, RoundingMode.DOWN).longValue();
        }
        return 0L;
    }
}
