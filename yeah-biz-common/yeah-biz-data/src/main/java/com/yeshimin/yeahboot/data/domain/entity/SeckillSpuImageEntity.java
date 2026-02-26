package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀商品SPU图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_seckill_spu_image")
public class SeckillSpuImageEntity extends ConditionBaseEntity<SeckillSpuImageEntity> {

    /**
     * 商户ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 原始SPU ID
     */
    private Long spuId;

    /**
     * 秒杀商品SPU ID
     */
    private Long seckillSpuId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 排序：大于等于1
     */
    private Integer sort;
}
