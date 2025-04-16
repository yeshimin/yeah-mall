package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.common.enums.DataStatusEnum;
import com.yeshimin.yeahboot.upms.common.validation.EnumValue;
import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 系统组织-创建-DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOrgCreateDto extends BaseDomain {

    /**
     * 父ID
     */
    private Long parentId;

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
     * 排序：自然数
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
