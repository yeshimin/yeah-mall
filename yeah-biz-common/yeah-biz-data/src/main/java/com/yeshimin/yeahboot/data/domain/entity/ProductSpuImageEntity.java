package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spu_image")
public class ProductSpuImageEntity extends ShopConditionBaseEntity<ProductSpuImageEntity> {

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 排序：大于等于1
     */
    private Integer sort;
}
