package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品spu创建DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuCreateDto extends ShopDataBaseDomain {

    /**
     * 商品分类ID
     */
    @NotNull(message = "商品分类ID不能为空", groups = {Create.class})
    private Long categoryId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空", groups = {Create.class})
    private String name;

    /**
     * 商品规格与选项
     */
    @Valid
//    @NotEmpty(message = "商品规格与选项不能为空", groups = {Create.class})
    private List<SpecOptDto> specs;
}
