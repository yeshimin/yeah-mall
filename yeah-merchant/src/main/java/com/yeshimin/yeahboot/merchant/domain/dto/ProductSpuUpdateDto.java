package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品spu更新DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuUpdateDto extends ShopDataBaseDomain {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = {Update.class})
    private Long id;

    /**
     * 商品分类ID
     */
//    @NotNull(message = "商品分类ID不能为空", groups = {Update.class})
    private Long categoryId;

    /**
     * 商品名称
     */
//    @NotBlank(message = "商品名称不能为空", groups = {Update.class})
    private String name;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 商品规格与选项
     */
    @Valid
//    @NotEmpty(message = "商品规格与选项不能为空", groups = {Update.class})
    private List<SpecOptDto> specs;

    /**
     * 详细描述
     */
//    @NotNull(message = "商品详细描述不能为空", groups = {Update.class})
    private String detailDesc;
}
