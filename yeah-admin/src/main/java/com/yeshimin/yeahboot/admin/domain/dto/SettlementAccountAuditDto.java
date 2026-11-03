package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 商家结算账户审核参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementAccountAuditDto extends BaseDomain {

    /**
     * 账户ID
     */
    @NotNull(message = "账户ID不能为空")
    private Long id;

    /**
     * 是否审核通过
     */
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    /**
     * 审核备注
     */
    @Size(max = 255, message = "审核备注不能超过255个字符")
    private String remark;
}
