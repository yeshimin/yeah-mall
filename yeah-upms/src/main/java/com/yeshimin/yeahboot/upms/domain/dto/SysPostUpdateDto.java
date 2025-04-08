package com.yeshimin.yeahboot.upms.domain.dto;

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
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;
}
