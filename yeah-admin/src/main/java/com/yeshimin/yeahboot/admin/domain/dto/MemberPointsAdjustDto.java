package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 平台调整会员积分参数
 */
@Data
public class MemberPointsAdjustDto {

    /**
     * 会员ID
     */
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    /**
     * 积分变动值，正数增加、负数扣减
     */
    @NotNull(message = "积分变动值不能为空")
    private Long changePoints;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注不能超过200个字符")
    private String remark;
}
