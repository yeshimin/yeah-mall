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

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 秒杀场次名称
     */
    private String sessionName;

    /**
     * 商家名称
     */
    private String mchName;

    /**
     * 主图
     */
    private String mainImage;

    /**
     * sku最低价-原价
     */
    private BigDecimal minOriginPrice;
}
