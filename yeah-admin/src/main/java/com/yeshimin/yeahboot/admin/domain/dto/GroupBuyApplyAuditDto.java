package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 拼团报名审核参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyApplyAuditDto extends BaseDomain {

    /**
     * 报名申请ID
     */
    @NotNull(message = "报名申请ID不能为空")
    private Long applyId;

    /**
     * 审核状态
     */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;
}
