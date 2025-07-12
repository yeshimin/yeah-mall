package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品规格选项VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpecOptVo extends BaseDomain {

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 选项ID
     */
    private Long optId;

    /**
     * 选项名称
     */
    private String optName;

    /**
     * 排序（自然序，从1开始）
     */
    private Integer sort;
}
