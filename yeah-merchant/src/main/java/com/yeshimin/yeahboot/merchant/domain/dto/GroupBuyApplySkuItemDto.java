package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 拼团报名SKU参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyApplySkuItemDto extends BaseDomain {

    /**
     * 原始商品SKU ID
     */
    @NotNull(message = "原始商品SKU ID不能为空")
    private Long skuId;

    /**
     * 拼团价格
     */
    @NotNull(message = "拼团价格不能为空")
    private BigDecimal groupPrice;

    /**
     * 拼团库存
     */
    @NotNull(message = "拼团库存不能为空")
    private Integer stock;

    /**
     * 自定义拼团SKU主图，为空时引用原始SKU主图
     */
    private String mainImage;
}
