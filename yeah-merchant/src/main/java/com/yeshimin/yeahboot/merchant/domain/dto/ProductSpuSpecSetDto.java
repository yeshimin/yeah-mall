package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品spu规格设置DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuSpecSetDto extends ShopDataBaseDomain {

    /**
     * 商品SPU ID
     */
    @NotNull(message = "商品SPU ID不能为空", groups = {Create.class})
    private Long spuId;

    /**
     * 商品规格与选项
     */
    @Valid
    @NotEmpty(message = "商品规格与选项不能为空", groups = {Create.class})
    private List<SpecOptDto> specs;
}
