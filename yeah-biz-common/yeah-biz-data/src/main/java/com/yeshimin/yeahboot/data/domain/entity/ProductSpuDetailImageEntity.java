package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU详情富文本图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spu_detail_image")
public class ProductSpuDetailImageEntity extends ShopConditionBaseEntity<ProductSpuDetailImageEntity> {

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 图片URL
     */
    private String imageUrl;
}
