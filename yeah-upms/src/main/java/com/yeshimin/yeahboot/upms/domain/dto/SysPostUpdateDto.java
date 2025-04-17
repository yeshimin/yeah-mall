package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.common.enums.DataStatusEnum;
import com.yeshimin.yeahboot.upms.common.validation.EnumValue;
import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysPostUpdateDto extends BaseDomain {

    /**
     * ID
     */
    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
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
