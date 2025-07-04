package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品规格定义表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spec_def")
public class ProductSpecDefEntity extends ConditionBaseEntity<ProductSpecDefEntity> {

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 规格名称
     */
    private String specName;
}
