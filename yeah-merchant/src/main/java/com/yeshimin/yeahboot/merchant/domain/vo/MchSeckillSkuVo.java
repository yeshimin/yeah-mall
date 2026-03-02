package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商家端-秒杀活动报名申请详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchSeckillSkuVo extends BaseDomain {

    /**
     * ID
     */
    private Long id;

    /**
     * 商户ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 原始商品SKU ID
     */
    private Long skuId;

    /**
     * 秒杀商品SPU ID
     */
    private Long seckillSpuId;

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 名称
     */
    private String name;

    /**
     * 规格编码
     */
    private String specCode;

    /**
     * 原始价格
     */
    private BigDecimal originPrice;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 秒杀库存
     */
    private Integer stock;

    /**
     * 主图
     */
    private String mainImage;
}
