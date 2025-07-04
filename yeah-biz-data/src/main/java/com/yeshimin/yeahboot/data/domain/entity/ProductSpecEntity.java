package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品规格表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spec")
public class ProductSpecEntity extends ConditionBaseEntity<ProductSpecEntity> {

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 规格ID
     */
    private Long specId;
}
