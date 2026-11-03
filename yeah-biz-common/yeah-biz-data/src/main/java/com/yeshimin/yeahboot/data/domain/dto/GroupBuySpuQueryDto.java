package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyApplyAuditStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拼团商品查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuySpuQueryDto extends BaseQueryDto {

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 审核状态
     */
    @EnumValue(enumClass = GroupBuyApplyAuditStatusEnum.class, message = "审核状态不正确")
    private Integer auditStatus;
}
