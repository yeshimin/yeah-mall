package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 积分商品分类保存参数
 */
@Data
public class PointsCategorySaveDto {

    /**
     * 分类ID，新增时为空
     */
    private Long id;

    /**
     * 父分类ID，0表示一级分类
     */
    @NotNull(message = "父分类ID不能为空")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 分类图标文件Key
     */
    private String icon;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isEnabled;
}
