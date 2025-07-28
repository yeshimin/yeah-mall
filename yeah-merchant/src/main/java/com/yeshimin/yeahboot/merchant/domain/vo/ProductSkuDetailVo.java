package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品sku详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSkuDetailVo extends ProductSkuEntity {

    /**
     * 规格配置信息
     */
    private List<ProductSkuSpecVo> specs;
}
