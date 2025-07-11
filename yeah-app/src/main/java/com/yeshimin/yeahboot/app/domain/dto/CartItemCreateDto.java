package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CartItemCreateDto extends BaseDomain {

    /**
     * skuId
     */
    @NotNull(message = "商品SKU ID不能为空")
    private Long skuId;

    /**
     * 数量
     */
    @Min(value = 1, message = "数量不能小于1")
    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
