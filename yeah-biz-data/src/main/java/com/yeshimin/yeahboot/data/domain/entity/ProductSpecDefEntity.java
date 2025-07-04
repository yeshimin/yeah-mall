package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 商品规格定义表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spec_def")
public class ProductSpecDefEntity extends ShopConditionBaseEntity<ProductSpecDefEntity> {

    /**
     * 规格名称
     */
    @NotBlank(message = "规格名称不能为空", groups = {Create.class})
    private String specName;
}
