package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
}
