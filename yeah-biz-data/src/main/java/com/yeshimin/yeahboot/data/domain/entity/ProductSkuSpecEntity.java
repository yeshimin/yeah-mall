package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SKU规格选项配置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_sku_spec")
public class ProductSkuSpecEntity extends ShopConditionBaseEntity<ProductSkuSpecEntity> {

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 商品SKU ID
     */
    private Long skuId;

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 选项ID
     */
    private Long optId;
}
