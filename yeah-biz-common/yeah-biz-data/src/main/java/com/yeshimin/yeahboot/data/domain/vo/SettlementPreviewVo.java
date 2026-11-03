package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 线下结算预览
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementPreviewVo extends BaseDomain {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 实际参与结算的台账数量
     */
    private Integer recordCount;

    /**
     * 自动纳入的负向台账数量
     */
    private Integer autoIncludedNegativeCount;

    /**
     * 实际结算净额
     */
    private BigDecimal totalAmount;

    /**
     * 结算账户ID
     */
    private Long settlementAccountId;

    /**
     * 结算账户类型
     */
    private String settlementAccountType;

    /**
     * 收款账户名称
     */
    @SensitiveData(type = SensitiveType.NAME, scenes = SensitiveScene.LOG)
    private String settlementAccountName;

    /**
     * 收款账号
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String settlementAccountNo;

    /**
     * 开户银行
     */
    private String settlementBankName;
}
