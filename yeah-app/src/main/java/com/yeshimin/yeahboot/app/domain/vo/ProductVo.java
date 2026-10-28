package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductVo extends BaseDomain {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 原始商品SPU ID；营销商品详情用于评价和收藏等原商品能力。
     */
    private Long sourceSpuId;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品分类名称
     */
    private String categoryName;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 销量
     */
    private Long sales;

    /**
     * sku最低价
     */
    private BigDecimal minPrice;

    /**
     * sku最高价
     */
    private BigDecimal maxPrice;

    /**
     * 主图
     */
    private String mainImage;

    /**
     * 详细描述
     */
    private String detailDesc;
}
