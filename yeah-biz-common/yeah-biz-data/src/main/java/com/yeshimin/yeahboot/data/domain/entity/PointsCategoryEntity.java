package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分商品分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_points_category")
public class PointsCategoryEntity extends ConditionBaseEntity<PointsCategoryEntity> {

    /**
     * 父分类ID，0表示一级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标文件Key
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}
