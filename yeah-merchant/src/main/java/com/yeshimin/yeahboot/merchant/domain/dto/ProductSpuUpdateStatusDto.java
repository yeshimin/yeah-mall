package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 商品上下架参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuUpdateStatusDto extends ShopDataBaseDomain {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /**
     * 是否上架
     */
    @NotNull(message = "商品状态不能为空")
    private Boolean isEnabled;
}
