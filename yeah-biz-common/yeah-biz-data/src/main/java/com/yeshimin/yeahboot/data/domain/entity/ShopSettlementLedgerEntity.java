package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveType;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家结算台账
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_shop_settlement_ledger")
public class ShopSettlementLedgerEntity extends ShopConditionBaseEntity<ShopSettlementLedgerEntity> {

    /**
     * 台账编号
     */
    private String ledgerNo;

    /**
     * 业务类型：1订单收入，2退款冲减，3人工调整
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 原始金额
     */
    private BigDecimal grossAmount;

    /**
     * 平台佣金比例，单位百分比
     */
    private BigDecimal commissionRate;

    /**
     * 平台佣金金额
     */
    private BigDecimal commissionAmount;

    /**
     * 台账变动金额，收入为正、冲减为负
     */
    private BigDecimal amount;

    /**
     * 可结算时间
     */
    private LocalDateTime availableTime;

    /**
     * 状态：1待结算，2已结算，3已取消
     */
    private String status;

    /**
     * 结算批次编号
     */
    private String settlementNo;

    /**
     * 线下转账流水号
     */
    private String transactionNo;

    /**
     * 本次打款使用的结算账户ID
     */
    private Long settlementAccountId;

    /**
     * 结算账户类型快照
     */
    private String settlementAccountType;

    /**
     * 结算账户名称快照
     */
    @SensitiveData(type = SensitiveType.NAME, scenes = SensitiveScene.LOG)
    private String settlementAccountName;

    /**
     * 结算账号快照
     */
    @JsonIgnore
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String settlementAccountNo;

    /**
     * 开户银行快照
     */
    private String settlementBankName;

    /**
     * 打款凭证文件Key
     */
    private String proof;

    /**
     * 结算时间
     */
    private LocalDateTime settledTime;

    /**
     * 结算备注
     */
    private String settlementRemark;
}
