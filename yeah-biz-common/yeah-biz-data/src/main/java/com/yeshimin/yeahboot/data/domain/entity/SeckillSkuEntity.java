package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 秒杀商品SKU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_seckill_sku")
public class SeckillSkuEntity extends ConditionBaseEntity<SeckillSkuEntity> {

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
