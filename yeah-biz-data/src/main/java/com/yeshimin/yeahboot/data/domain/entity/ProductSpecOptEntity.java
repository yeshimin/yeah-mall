package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品规格选项表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spec_opt")
public class ProductSpecOptEntity extends ShopConditionBaseEntity<ProductSpecOptEntity> {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 选项ID
     */
    private Long optId;
}
