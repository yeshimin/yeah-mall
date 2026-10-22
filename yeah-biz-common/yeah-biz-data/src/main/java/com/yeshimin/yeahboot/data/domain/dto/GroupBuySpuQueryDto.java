package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
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
    private Integer auditStatus;
}
