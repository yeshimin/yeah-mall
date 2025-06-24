package com.yeshimin.yeahboot.merchant.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.merchant.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spu")
public class ProductSpuEntity extends ShopConditionBaseEntity<ProductSpuEntity> {

    /**
     * 商品名称
     */
    private String name;
}
