package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 商品spu规格查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuSpecQueryDto extends ShopDataBaseDomain {

    /**
     * 商品SPU ID
     */
    @NotNull(message = "商品SPU ID不能为空", groups = {Query.class})
    private Long spuId;
}
