package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.common.enums.DataStatusEnum;
import com.yeshimin.yeahboot.upms.common.validation.EnumValue;
import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysPostCreateDto extends BaseDomain {

    /**
     * 编码
     */
    @NotBlank(message = "编码不能为空")
    private String code;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 状态：1-启用 2-禁用
     */
    @EnumValue(enumClass = DataStatusEnum.class)
    private String status;

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
