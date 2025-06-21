package com.yeshimin.yeahboot.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品SKU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_sku")
public class ProductSkuEntity extends ConditionBaseEntity<ProductSkuEntity> {

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 名称
     */
    private String name;

    /**
     * 规格
     */
    private String specs;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;
}
