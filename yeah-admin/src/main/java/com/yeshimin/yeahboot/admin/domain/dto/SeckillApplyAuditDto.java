package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillApplyAuditDto extends BaseDomain {

    /**
     * 申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long applyId;

    /**
     * 审核状态，见枚举：SeckillActivityApplyAuditStatusEnum
     */
    @EnumValue(enumClass = SeckillActivityApplyAuditStatusEnum.class)
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;
}
