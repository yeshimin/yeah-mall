package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivityApplyQueryDto extends BaseQueryDto {

    /**
     * 秒杀活动ID
     */
//    @NotNull(message = "秒杀活动ID不能为空")
    private Long activityId;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 审核状态
     */
    @EnumValue(enumClass = SeckillActivityApplyAuditStatusEnum.class)
    private Integer auditStatus;
}
