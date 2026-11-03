package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * App拼团活动视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyActivityVo extends BaseDomain {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动封面
     */
    private String coverImage;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityBeginTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;

    /**
     * 成团人数
     */
    private Integer minPeople;

    /**
     * 最大开团数量
     */
    private Integer groupLimit;

    /**
     * 活动状态
     */
    private String status;
}
