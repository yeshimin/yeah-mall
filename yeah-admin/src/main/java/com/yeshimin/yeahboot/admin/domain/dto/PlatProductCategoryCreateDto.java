package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatProductCategoryCreateDto extends BaseDomain {

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 编码
     */
    @NotBlank(message = "编码不能为空")
    private String code;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 排序：大于等于1
     */
    @Min(value = 1, message = "排序必须大于等于1")
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
