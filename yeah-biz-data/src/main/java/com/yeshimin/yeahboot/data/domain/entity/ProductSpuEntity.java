package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商品SPU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spu")
public class ProductSpuEntity extends ShopConditionBaseEntity<ProductSpuEntity> {

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
}
