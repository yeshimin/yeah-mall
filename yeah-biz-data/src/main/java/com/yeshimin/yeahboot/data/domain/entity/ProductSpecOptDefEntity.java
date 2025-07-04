package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商品规格选项定义表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_spec_opt_def")
public class ProductSpecOptDefEntity extends ShopConditionBaseEntity<ProductSpecOptDefEntity> {

    /**
     * 规格ID
     */
    @NotNull(message = "规格ID不能为空", groups = {Create.class})
    private Long specId;

    /**
     * 选项名称
     */
    @NotBlank(message = "选项名称不能为空", groups = {Create.class})
    private String optName;
}
