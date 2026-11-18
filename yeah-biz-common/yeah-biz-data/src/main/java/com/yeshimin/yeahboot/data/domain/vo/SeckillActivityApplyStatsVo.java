package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀活动报名申请统计VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivityApplyStatsVo extends BaseDomain {

    /**
     * 当前筛选条件下的报名总数
     */
    private Long total;

    /**
     * 当前筛选条件下的待审核数
     */
    private Long pending;

    /**
     * 当前筛选条件下的审核通过数
     */
    private Long passed;

    /**
     * 当前筛选条件下的审核驳回数
     */
    private Long rejected;

    /**
     * 当前活动的报名总数（不受筛选条件影响）
     */
    private Long activityTotal;

    /**
     * 当前活动的待审核数（不受筛选条件影响）
     */
    private Long activityPending;
}
