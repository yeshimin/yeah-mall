package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家库存预警项
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchStockWarningVo extends BaseDomain {

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 商品名称
     */
    private String spuName;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 当前库存
     */
    private Integer stock;
}
