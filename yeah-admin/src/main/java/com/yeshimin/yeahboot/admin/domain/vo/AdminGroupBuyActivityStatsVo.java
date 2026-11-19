package com.yeshimin.yeahboot.admin.domain.vo;

import lombok.Data;

/**
 * 拼团活动状态统计
 */
@Data
public class AdminGroupBuyActivityStatsVo {

    /**
     * 全部活动数量
     */
    private Long total;

    /**
     * 新建
     */
    private Long created;

    /**
     * 已发布
     */
    private Long published;

    /**
     * 报名中
     */
    private Long applyStarted;

    /**
     * 报名结束
     */
    private Long applyFinished;

    /**
     * 进行中
     */
    private Long activityStarted;

    /**
     * 已结束
     */
    private Long activityFinished;

    /**
     * 已下架
     */
    private Long activityDown;

    /**
     * 新建且发布规则未完善的活动数量
     */
    private Long pendingSetup;
}