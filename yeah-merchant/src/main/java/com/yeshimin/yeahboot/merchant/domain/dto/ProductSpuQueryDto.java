package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品spu查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuQueryDto extends ShopDataBaseDomain {

    /**
     * 商品分类ID
     */
    @QueryField(type = QueryField.Type.EQ)
    private Long categoryId;

    /**
     * 商品名称
     */
    @QueryField(type = QueryField.Type.LIKE)
    private String name;
}
