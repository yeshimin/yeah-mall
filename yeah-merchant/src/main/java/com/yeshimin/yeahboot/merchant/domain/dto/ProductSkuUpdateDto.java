package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品sku更新DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSkuUpdateDto extends ShopDataBaseDomain {

    /**
     * 商品SKU ID
     */
    @NotNull(message = "商品SKU ID不能为空")
    private Long skuId;

    /**
     * 规格选项ID集合
     */
    @NotEmpty(message = "规格选项ID集合不能为空")
    private List<Long> optIds;

    /**
     * 名称
     */
    private String name;

    /**
     * 价格
     */
    @Min(value = 0, message = "价格不能小于0")
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    private Integer stock;
}
