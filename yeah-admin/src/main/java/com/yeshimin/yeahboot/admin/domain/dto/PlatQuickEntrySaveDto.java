package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.admin.common.enums.PlatQuickEntryTypeEnum;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatQuickEntrySaveDto extends BaseDomain {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = Update.class)
    private Long id;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = {Create.class})
    private String name;

    /**
     * 图标
     */
    @NotBlank(message = "图标不能为空", groups = {Create.class})
    private String icon;

    /**
     * 业务类型
     */
    @EnumValue(enumClass = PlatQuickEntryTypeEnum.class, groups = {Create.class, Update.class})
    @NotNull(message = "业务类型不能为空", groups = {Create.class})
    private Integer type;

    /**
     * 跳转目标
     */
    private String target;

    /**
     * 排序：大于等于1
     */
    @Min(value = 1, message = "排序必须大于等于1", groups = {Create.class, Update.class})
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 备注
     */
    private String remark;
}
