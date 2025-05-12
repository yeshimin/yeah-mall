package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AreaVo extends BaseDomain {

    /**
     * 父编码
     */
    private String parentCode;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 子集合
     */
    private List<AreaVo> children;
}
