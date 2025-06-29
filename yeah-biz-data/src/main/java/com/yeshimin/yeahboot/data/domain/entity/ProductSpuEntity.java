package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 商品SPU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spu")
public class ProductSpuEntity extends ShopConditionBaseEntity<ProductSpuEntity> {

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空", groups = {Create.class})
    private String name;
}
