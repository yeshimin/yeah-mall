package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品规格表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spec")
public class ProductSpecEntity extends ShopConditionBaseEntity<ProductSpecEntity> {

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 规格ID
     */
    private Long specId;
}
