package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillApplySkuItemDto extends BaseDomain {

    /**
     * 原始商品SKU ID
     */
    @NotNull(message = "原始商品SKU ID")
    private Long skuId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 规格编码
     */
    @NotBlank(message = "规格编码不能为空")
    private String specCode;

    /**
     * 原始价格
     */
    @NotNull(message = "原始价格不能为空")
    private BigDecimal originPrice;

    /**
     * 秒杀价格
     */
    @NotNull(message = "秒杀价格不能为空")
    private BigDecimal seckillPrice;

    /**
     * 秒杀库存
     */
    @NotNull(message = "秒杀库存不能为空")
    private Integer stock;

    /**
     * 主图
     */
    @NotBlank(message = "主图不能为空")
    private String mainImage;
}
