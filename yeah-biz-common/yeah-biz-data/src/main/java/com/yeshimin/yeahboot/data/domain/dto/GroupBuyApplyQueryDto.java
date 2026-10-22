package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拼团报名查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyApplyQueryDto extends BaseQueryDto {

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商家名称
     */
    private String mchName;

    /**
     * 审核状态
     */
    private Integer auditStatus;
}
