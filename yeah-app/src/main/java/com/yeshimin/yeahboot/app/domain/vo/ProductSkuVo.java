package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * App端商品SKU VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSkuVo extends BaseDomain {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 规格编码
     */
    private String specCode;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 主图
     */
    private String mainImage;
}
