package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀活动成交动态（最近支付成功的秒杀订单）
 */
@Data
public class SeckillOrderEventVo {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付成功时间
     */
    private String payTime;

    /**
     * 会员昵称
     */
    private String memberName;

    /**
     * 秒杀商品名称
     */
    private String spuName;

    /**
     * 秒杀场次名称
     */
    private String sessionName;

    /**
     * 成交数量
     */
    private Long quantity;

    /**
     * 成交金额
     */
    private BigDecimal totalPrice;
}