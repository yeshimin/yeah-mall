package com.yeshimin.yeahboot.app.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecOptVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopCartItemVo extends BaseDomain {

    /**
     * 店铺ID
     */
    @JsonIgnore
    private Long shopId;

    /**
     * 店铺名称
     */
    @JsonIgnore
    private String shopName;

    /**
     * spu ID
     */
    private Long spuId;

    /**
     * spu名称
     */
    private String spuName;

    /**
     * sku ID
     */
    private Long skuId;

    /**
     * sku名称
     */
    private String skuName;

    /**
     * 规格
     */
    private List<ProductSpecOptVo> specs;

    /**
     * 价格（单价）
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;
}
