package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品规格VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpecVo extends BaseDomain {

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 选项集合
     */
    private List<ProductSpecOptVo> opts;
}
