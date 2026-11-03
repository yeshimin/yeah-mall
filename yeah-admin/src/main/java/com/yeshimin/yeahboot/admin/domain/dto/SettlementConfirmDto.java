package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * 确认线下结算参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementConfirmDto extends BaseDomain {

    /**
     * 待结算台账ID集合
     */
    @NotEmpty(message = "请选择待结算记录")
    private Collection<Long> ids;

    /**
     * 预览得到的实际结算净额
     */
    @NotNull(message = "预览结算金额不能为空")
    private BigDecimal expectedAmount;

    /**
     * 预览得到的实际结算记录数
     */
    @NotNull(message = "预览结算记录数不能为空")
    private Integer expectedRecordCount;

    /**
     * 预览时锁定的结算账户ID
     */
    @NotNull(message = "结算账户ID不能为空")
    private Long settlementAccountId;

    /**
     * 线下转账流水号
     */
    @NotBlank(message = "线下转账流水号不能为空")
    private String transactionNo;

    /**
     * 打款凭证文件Key
     */
    private String proof;

    /**
     * 结算备注
     */
    private String remark;
}
