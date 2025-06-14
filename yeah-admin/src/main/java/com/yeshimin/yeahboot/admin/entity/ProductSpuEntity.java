package com.yeshimin.yeahboot.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spu")
public class ProductSpuEntity extends ConditionBaseEntity<ProductSpuEntity> {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商品名称
     */
    private String name;
}
