package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品SKU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_sku")
public class ProductSkuEntity extends ShopConditionBaseEntity<ProductSkuEntity> {

    /**
     * 商品SPU ID
     */
    @NotNull(message = "商品SPU ID不能为空", groups = {Create.class, Query.class})
    private Long spuId;

    /**
     * 名称
     */
    private String name;

    /**
     * 规格编码
     */
    private String specCode;

    /**
     * 价格
     */
    @Min(value = 0, message = "价格不能小于0", groups = {Create.class})
    @NotNull(message = "价格不能为空", groups = {Create.class})
    private BigDecimal price;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空", groups = {Create.class})
    private Integer stock;
}
