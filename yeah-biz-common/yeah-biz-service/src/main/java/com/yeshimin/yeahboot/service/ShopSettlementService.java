package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.data.common.enums.OrderSettlementModeEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.SettlementLedgerBizTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.SettlementLedgerStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.SettlementAccountStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementAccountEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementLedgerEntity;
import com.yeshimin.yeahboot.data.domain.vo.SettlementPreviewVo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.ShopSettlementAccountRepo;
import com.yeshimin.yeahboot.data.repository.ShopSettlementLedgerRepo;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商家结算服务
 */
@Service
@RequiredArgsConstructor
public class ShopSettlementService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final ShopSettlementLedgerRepo ledgerRepo;
    private final OrderRepo orderRepo;
    private final ShopSettlementAccountRepo accountRepo;
    private final IdService idService;
    private final StorageManager storageManager;

    /**
     * 订单完成后记录待结算收入
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordOrderCompleted(OrderEntity order) {
        if (!OrderSettlementModeEnum.MANUAL.getValue().equals(order.getSettlementMode())) {
            return;
        }
        // 业务类型和订单ID共同保证重复完成通知不会重复记账。
        if (ledgerRepo.findOneByBiz(SettlementLedgerBizTypeEnum.ORDER.getValue(), order.getId()) != null) {
            return;
        }

        // 佣金和账期均使用下单时快照，后续修改店铺配置不影响历史订单。
        BigDecimal payAmount = this.requirePayAmount(order);
        BigDecimal commissionRate = this.normalizeCommissionRate(order.getSettlementCommissionRate());
        BigDecimal commissionAmount = this.calculateCommission(payAmount, commissionRate);
        int delayDays = order.getSettlementDelayDays() == null
                ? 0 : Math.max(order.getSettlementDelayDays(), 0);

        ShopSettlementLedgerEntity ledger = new ShopSettlementLedgerEntity();
        ledger.setMchId(order.getMchId());
        ledger.setShopId(order.getShopId());
        ledger.setLedgerNo(idService.nextEncodedId());
        ledger.setBizType(SettlementLedgerBizTypeEnum.ORDER.getValue());
        ledger.setBizId(order.getId());
        ledger.setOrderId(order.getId());
        ledger.setOrderNo(order.getOrderNo());
        ledger.setGrossAmount(payAmount);
        ledger.setCommissionRate(commissionRate);
        ledger.setCommissionAmount(commissionAmount);
        BigDecimal settlementAmount = payAmount.subtract(commissionAmount);
        ledger.setAmount(settlementAmount);
        ledger.setAvailableTime(LocalDateTime.now().plusDays(delayDays));
        ledger.setStatus(settlementAmount.compareTo(BigDecimal.ZERO) > 0
                ? SettlementLedgerStatusEnum.PENDING.getValue()
                : SettlementLedgerStatusEnum.CANCELED.getValue());
        ledger.insert();
    }

    /**
     * 退款成功后冲减商家结算金额
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordRefundCompleted(OrderEntity order, OrderRefundEntity refund) {
        if (!OrderSettlementModeEnum.MANUAL.getValue().equals(order.getSettlementMode())) {
            return;
        }
        ShopSettlementLedgerEntity income = ledgerRepo.findOneByBiz(
                SettlementLedgerBizTypeEnum.ORDER.getValue(), order.getId());
        if (income == null) {
            return;
        }

        BigDecimal refundAmount = refund.getRefundAmount() == null
                ? this.requirePayAmount(order) : refund.getRefundAmount().abs();
        refundAmount = refundAmount.min(this.requirePayAmount(order));

        if (SettlementLedgerStatusEnum.PENDING.getValue().equals(income.getStatus())) {
            // 原收入尚未结算时直接冲减原台账，避免产生无意义的正负两条待结算记录。
            BigDecimal remainingGross = income.getGrossAmount().subtract(refundAmount).max(BigDecimal.ZERO);
            BigDecimal commissionAmount = this.calculateCommission(remainingGross, income.getCommissionRate());
            income.setGrossAmount(remainingGross);
            income.setCommissionAmount(commissionAmount);
            income.setAmount(remainingGross.subtract(commissionAmount));
            if (income.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                income.setStatus(SettlementLedgerStatusEnum.CANCELED.getValue());
            }
            income.updateById();
            return;
        }

        if (ledgerRepo.findOneByBiz(SettlementLedgerBizTypeEnum.REFUND.getValue(), refund.getId()) != null) {
            return;
        }
        // 原收入已结算时新增负台账，后续结算自动从商家应结金额中扣回。
        ShopSettlementLedgerEntity ledger = new ShopSettlementLedgerEntity();
        ledger.setMchId(order.getMchId());
        ledger.setShopId(order.getShopId());
        ledger.setLedgerNo(idService.nextEncodedId());
        ledger.setBizType(SettlementLedgerBizTypeEnum.REFUND.getValue());
        ledger.setBizId(refund.getId());
        ledger.setOrderId(order.getId());
        ledger.setOrderNo(order.getOrderNo());
        ledger.setGrossAmount(refundAmount.negate());
        BigDecimal refundCommission = this.calculateCommission(refundAmount, income.getCommissionRate());
        BigDecimal merchantRefundAmount = refundAmount.subtract(refundCommission);
        ledger.setCommissionRate(income.getCommissionRate());
        ledger.setCommissionAmount(refundCommission.negate());
        ledger.setAmount(merchantRefundAmount.negate());
        ledger.setAvailableTime(LocalDateTime.now());
        ledger.setStatus(SettlementLedgerStatusEnum.PENDING.getValue());
        ledger.insert();
    }

    /**
     * 预览线下结算金额
     */
    public SettlementPreviewVo preview(Collection<Long> ids) {
        PreparedSettlement prepared = this.prepareSettlement(ids, LocalDateTime.now());
        SettlementPreviewVo vo = new SettlementPreviewVo();
        vo.setShopId(prepared.shopId);
        vo.setRecordCount(prepared.ledgers.size());
        vo.setAutoIncludedNegativeCount(prepared.autoIncludedNegativeCount);
        vo.setTotalAmount(prepared.totalAmount);
        vo.setSettlementAccountId(prepared.account.getId());
        vo.setSettlementAccountType(prepared.account.getAccountType());
        vo.setSettlementAccountName(prepared.account.getAccountName());
        vo.setSettlementAccountNo(prepared.account.getAccountNo());
        vo.setSettlementBankName(prepared.account.getBankName());
        return vo;
    }

    /**
     * 确认线下打款
     */
    @Transactional(rollbackFor = Exception.class)
    public String settle(Collection<Long> ids, BigDecimal expectedAmount, Integer expectedRecordCount,
                         Long settlementAccountId,
                         String transactionNo, String proof, String remark) {
        if (ids == null || ids.isEmpty()) {
            throw new BaseException("请选择待结算记录");
        }
        if (StrUtil.isBlank(transactionNo)) {
            throw new BaseException("线下转账流水号不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        PreparedSettlement prepared = this.prepareSettlement(ids, now);
        // 以预览金额、记录数和账户ID防止预览后数据变化仍继续打款。
        if (expectedAmount == null || expectedAmount.compareTo(prepared.totalAmount) != 0
                || !Objects.equals(expectedRecordCount, prepared.ledgers.size())
                || !Objects.equals(settlementAccountId, prepared.account.getId())) {
            throw new BaseException("结算金额或记录已变化，请重新预览后再打款");
        }

        if (StrUtil.isNotBlank(proof)) {
            storageManager.markUse(proof);
        }
        String settlementNo = idService.nextEncodedId();
        for (ShopSettlementLedgerEntity ledger : prepared.ledgers) {
            // 状态条件更新避免重复点击或并发结算同一条台账。
            boolean updated = ledgerRepo.lambdaUpdate()
                    .eq(ShopSettlementLedgerEntity::getId, ledger.getId())
                    .eq(ShopSettlementLedgerEntity::getStatus, SettlementLedgerStatusEnum.PENDING.getValue())
                    .set(ShopSettlementLedgerEntity::getStatus, SettlementLedgerStatusEnum.SETTLED.getValue())
                    .set(ShopSettlementLedgerEntity::getSettlementNo, settlementNo)
                    .set(ShopSettlementLedgerEntity::getTransactionNo, transactionNo.trim())
                    .set(ShopSettlementLedgerEntity::getSettlementAccountId, prepared.account.getId())
                    .set(ShopSettlementLedgerEntity::getSettlementAccountType, prepared.account.getAccountType())
                    .set(ShopSettlementLedgerEntity::getSettlementAccountName, prepared.account.getAccountName())
                    .set(ShopSettlementLedgerEntity::getSettlementAccountNo,
                            prepared.account.getAccountNo())
                    .set(ShopSettlementLedgerEntity::getSettlementBankName, prepared.account.getBankName())
                    .set(ShopSettlementLedgerEntity::getProof, StrUtil.nullToEmpty(proof))
                    .set(ShopSettlementLedgerEntity::getSettledTime, now)
                    .set(ShopSettlementLedgerEntity::getSettlementRemark, StrUtil.nullToEmpty(remark))
                    .update();
            if (!updated) {
                throw new BaseException("结算记录状态已变化，请刷新后重试");
            }
        }
        return settlementNo;
    }

    /**
     * 校验并准备一次线下结算所需的账户和台账
     */
    private PreparedSettlement prepareSettlement(Collection<Long> ids, LocalDateTime now) {
        if (ids == null || ids.isEmpty()) {
            throw new BaseException("请选择待结算记录");
        }
        Set<Long> selectedIds = ids.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (selectedIds.size() != ids.size()) {
            throw new BaseException("结算记录ID不能为空或重复");
        }
        List<ShopSettlementLedgerEntity> selected = ledgerRepo.findListByIds(selectedIds);
        if (selected.size() != selectedIds.size()) {
            throw new BaseException("包含无效的结算记录");
        }
        Set<Long> shopIds = selected.stream().map(ShopSettlementLedgerEntity::getShopId)
                .collect(Collectors.toSet());
        if (shopIds.size() != 1) {
            throw new BaseException("一次只能结算同一家店铺");
        }
        Long shopId = shopIds.iterator().next();
        ShopSettlementAccountEntity account = accountRepo.findOneByShopId(shopId);
        if (account == null || !SettlementAccountStatusEnum.APPROVED.getValue().equals(account.getStatus())) {
            throw new BaseException("店铺尚未配置已审核通过的结算账户");
        }

        Map<Long, ShopSettlementLedgerEntity> ledgerMap = selected.stream()
                .collect(Collectors.toMap(ShopSettlementLedgerEntity::getId, ledger -> ledger,
                        (first, second) -> first, LinkedHashMap::new));
        // 到期负台账必须自动纳入，避免平台只结算收入而遗漏已退款扣款。
        List<ShopSettlementLedgerEntity> negativeLedgers = ledgerRepo.lambdaQuery()
                .eq(ShopSettlementLedgerEntity::getShopId, shopId)
                .eq(ShopSettlementLedgerEntity::getStatus, SettlementLedgerStatusEnum.PENDING.getValue())
                .le(ShopSettlementLedgerEntity::getAvailableTime, now)
                .lt(ShopSettlementLedgerEntity::getAmount, BigDecimal.ZERO)
                .list();
        int autoIncludedNegativeCount = 0;
        for (ShopSettlementLedgerEntity ledger : negativeLedgers) {
            if (ledgerMap.putIfAbsent(ledger.getId(), ledger) == null) {
                autoIncludedNegativeCount++;
            }
        }

        List<ShopSettlementLedgerEntity> ledgers = new ArrayList<>(ledgerMap.values());
        for (ShopSettlementLedgerEntity ledger : ledgers) {
            if (!SettlementLedgerStatusEnum.PENDING.getValue().equals(ledger.getStatus())) {
                throw new BaseException("包含非待结算记录");
            }
            if (ledger.getAvailableTime() != null && ledger.getAvailableTime().isAfter(now)) {
                throw new BaseException("包含尚未到可结算时间的记录");
            }
            this.validateOrderForSettlement(ledger);
        }
        BigDecimal totalAmount = ledgers.stream().map(ShopSettlementLedgerEntity::getAmount)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseException("当前店铺结算净额必须大于0");
        }
        return new PreparedSettlement(shopId, account, ledgers, autoIncludedNegativeCount, totalAmount);
    }

    /**
     * 校验正向订单台账当前仍满足结算条件
     */
    private void validateOrderForSettlement(ShopSettlementLedgerEntity ledger) {
        if (!SettlementLedgerBizTypeEnum.ORDER.getValue().equals(ledger.getBizType())
                || ledger.getAmount() == null || ledger.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        OrderEntity order = orderRepo.getOneById(ledger.getOrderId(), "结算关联订单不存在");
        boolean completed = OrderStatusEnum.COMPLETED.getValue().equals(order.getStatus());
        boolean refundRejected = OrderStatusEnum.REFUND.getValue().equals(order.getStatus())
                && RefundStatusEnum.REJECTED.getValue().equals(order.getRefundStatus());
        if (!completed && !refundRejected) {
            throw new BaseException("订单正在退款、售后或状态已变化，暂不可结算：" + order.getOrderNo());
        }
    }

    /**
     * 已准备的线下结算数据
     */
    private static class PreparedSettlement {
        private final Long shopId;
        private final ShopSettlementAccountEntity account;
        private final List<ShopSettlementLedgerEntity> ledgers;
        private final int autoIncludedNegativeCount;
        private final BigDecimal totalAmount;

        /**
         * 创建已准备的线下结算数据
         */
        private PreparedSettlement(Long shopId, ShopSettlementAccountEntity account,
                                   List<ShopSettlementLedgerEntity> ledgers,
                                   int autoIncludedNegativeCount, BigDecimal totalAmount) {
            this.shopId = shopId;
            this.account = account;
            this.ledgers = ledgers;
            this.autoIncludedNegativeCount = autoIncludedNegativeCount;
            this.totalAmount = totalAmount;
        }
    }

    /**
     * 获取并规范化订单实际支付金额
     */
    private BigDecimal requirePayAmount(OrderEntity order) {
        if (order.getPayAmount() == null || order.getPayAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseException("订单实际支付金额无效");
        }
        return order.getPayAmount().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 校验并规范化平台佣金比例
     */
    private BigDecimal normalizeCommissionRate(BigDecimal commissionRate) {
        if (commissionRate == null) {
            return BigDecimal.ZERO;
        }
        if (commissionRate.compareTo(BigDecimal.ZERO) < 0
                || commissionRate.compareTo(ONE_HUNDRED) > 0) {
            throw new BaseException("平台结算佣金比例配置错误");
        }
        return commissionRate.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 按佣金比例计算平台佣金金额
     */
    private BigDecimal calculateCommission(BigDecimal amount, BigDecimal commissionRate) {
        return amount.multiply(commissionRate)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP)
                .min(amount);
    }
}
