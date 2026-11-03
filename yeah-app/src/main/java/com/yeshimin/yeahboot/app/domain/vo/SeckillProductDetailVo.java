package com.yeshimin.yeahboot.app.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * App端秒杀商品详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillProductDetailVo extends ProductDetailVo {

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 秒杀活动名称
     */
    private String activityName;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityBeginTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 秒杀场次名称
     */
    private String sessionName;

    /**
     * 场次开始时间
     */
    private LocalDateTime sessionBeginTime;

    /**
     * 场次结束时间
     */
    private LocalDateTime sessionEndTime;

    /**
     * 当前服务器时间
     */
    private LocalDateTime serverTime;

    /**
     * 当前实时剩余库存
     */
    private Integer remainingStock;
}
