package com.yeshimin.yeahboot.merchant.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_category")
public class ProductCategoryEntity extends ConditionBaseEntity<ProductCategoryEntity> {

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序：自然数
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
