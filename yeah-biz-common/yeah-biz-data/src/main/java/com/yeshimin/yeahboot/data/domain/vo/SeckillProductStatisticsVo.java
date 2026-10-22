package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀活动商品统计
 */
@Data
public class SeckillProductStatisticsVo {

    /**
     * 秒杀商品SPU ID
     */
    private Long seckillSpuId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 成交数量
     */
    private Long salesCount;

    /**
     * 成交金额
     */
    private BigDecimal salesAmount;

    /**
     * 剩余库存
     */
    private Long remainingStock;
}
