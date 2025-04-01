package com.yeshimin.yeahboot.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleResTreeNodeVo extends SysResTreeNodeVo {

    /**
     * 是否勾选
     */
    private Boolean checked;
}
