package com.yeshimin.yeahboot.merchant.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MchRoleResTreeNodeVo extends MchResTreeNodeVo {

    /**
     * 是否勾选
     */
    private Boolean checked;
}
