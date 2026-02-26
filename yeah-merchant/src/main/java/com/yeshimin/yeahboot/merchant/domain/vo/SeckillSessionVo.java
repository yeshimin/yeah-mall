package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillSessionVo extends BaseDomain {

    /**
     * ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动场次名称
     */
    private String name;

    /**
     * 活动开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 活动截止时间
     */
    private LocalDateTime endTime;

    /**
     * 排序：大于等于1
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}
