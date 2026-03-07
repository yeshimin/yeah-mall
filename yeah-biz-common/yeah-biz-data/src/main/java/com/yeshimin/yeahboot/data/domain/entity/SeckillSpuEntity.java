package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 秒杀商品SPU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_seckill_spu")
public class SeckillSpuEntity extends ShopConditionBaseEntity<SeckillSpuEntity> {

    /**
     * 原始SPU ID
     */
    private Long spuId;

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 销量
     */
    private Long sales;

    /**
     * sku最低价-原价
     */
    private BigDecimal minOriginPrice;

    /**
     * sku最低价-秒杀价
     */
    private BigDecimal minSeckillPrice;

    /**
     * sku最高价-原价
     */
    private BigDecimal maxOriginPrice;

    /**
     * sku最高价-秒杀价
     */
    private BigDecimal maxSeckillPrice;

    /**
     * 主图
     */
    private String mainImage;

    /**
     * 商品详细描述
     */
    private String detailDesc;

    /**
     * 审核状态，见枚举：SeckillActivityApplyAuditStatusEnum
     */
    private Integer auditStatus;
}
