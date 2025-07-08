package com.yeshimin.yeahboot.merchant.domain.vo;

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
     * 选项ID
     */
    private Long optId;

    /**
     * 选项名称
     */
    private String optName;
}
