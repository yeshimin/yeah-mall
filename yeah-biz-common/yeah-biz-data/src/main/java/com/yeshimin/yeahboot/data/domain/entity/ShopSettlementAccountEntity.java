package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商家线下结算收款账户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_shop_settlement_account")
public class ShopSettlementAccountEntity extends ShopConditionBaseEntity<ShopSettlementAccountEntity> {

    /**
     * 账户类型：1银行卡，2支付宝
     */
    private String accountType;

    /**
     * 收款人或账户名称
     */
    private String accountName;

    /**
     * 收款账号
     */
    @JsonIgnore
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String accountNo;

    /**
     * 开户银行，支付宝账户可为空
     */
    private String bankName;

    /**
     * 审核状态：1待审核，2已通过，3已拒绝
     */
    private String status;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;
}
