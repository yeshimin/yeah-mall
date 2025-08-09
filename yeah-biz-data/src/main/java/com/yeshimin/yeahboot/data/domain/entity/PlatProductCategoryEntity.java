package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台商品分类表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("plat_product_category")
public class PlatProductCategoryEntity extends ConditionBaseEntity<PlatProductCategoryEntity> {

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 编码
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 路径
     */
    private String path;

    /**
     * 排序：大于等于1
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
