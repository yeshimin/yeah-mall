package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家查询商品SKU DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSkuQueryDto extends ShopDataBaseDomain {

    /**
     * 商品SPU ID
     */
    @QueryField(type = QueryField.Type.EQ)
    private Long spuId;

    /**
     * SKU名称
     */
    @QueryField(type = QueryField.Type.LIKE)
    private String name;

    /**
     * 规格编码
     */
    @QueryField(type = QueryField.Type.LIKE)
    private String specCode;

    /**
     * 最大库存
     */
    private Integer stockMax;
}
