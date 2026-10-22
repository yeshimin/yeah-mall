package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀活动订单和库存统计
 */
@Data
public class SeckillStatisticsVo {

    /**
     * 已支付订单数
     */
    private Long paidOrderCount;

    /**
     * 已支付参与会员数
     */
    private Long participantCount;

    /**
     * 已支付成交金额
     */
    private BigDecimal salesAmount;

    /**
     * 秒杀剩余库存
     */
    private Long remainingStock;
}
