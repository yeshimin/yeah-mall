package com.yeshimin.yeahboot.upms.domain.dto;

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
     * 备注
     */
    private String remark;
}
