package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 拼团报名详情视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyApplyDetailVo extends GroupBuyApplyVo {

    /**
     * 活动状态
     */
    private String activityStatus;

    /**
     * 活动成团人数
     */
    private Integer activityMinPeople;

    /**
     * 活动最大开团数
     */
    private Integer activityGroupLimit;

    /**
     * 报名开始时间
     */
    private LocalDateTime applyBeginTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime applyEndTime;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityBeginTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;

    /**
     * 报名SKU列表
     */
    private List<GroupBuySkuEntity> skus;
}