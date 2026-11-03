package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.app.common.enums.ShopProductSortEnum;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 店铺商品查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopProductQueryDto extends BaseDomain {

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    /**
     * 店铺商品分类ID
     */
    private Long categoryId;

    /**
     * 商品关键词
     */
    private String keyword;

    /**
     * 排序选项
     */
    @EnumValue(enumClass = ShopProductSortEnum.class, message = "商品排序枚举不正确")
    private String sortBy;
}
