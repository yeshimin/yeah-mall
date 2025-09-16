package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

/**
 * 商品详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetailVo extends BaseDomain {

    /**
     * 商品基础信息
     */
    private ProductVo product;

    /**
     * 商品轮播图信息
     */
    private List<String> banners;

    /**
     * 商品规格信息
     */
    private List<ProductSpecVo> specs;

    /**
     * 商品SKU信息
     */
    private List<ProductSkuVo> skus;

    /**
     * 商品SKU规格信息
     */
    private Set<Long> skuOptIds;
}
