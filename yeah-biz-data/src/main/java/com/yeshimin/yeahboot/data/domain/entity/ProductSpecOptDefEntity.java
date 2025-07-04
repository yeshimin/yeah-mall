package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品规格选项定义表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spec_opt_def")
public class ProductSpecOptDefEntity extends ConditionBaseEntity<ProductSpecOptDefEntity> {

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 选项名称
     */
    private String optName;
}
