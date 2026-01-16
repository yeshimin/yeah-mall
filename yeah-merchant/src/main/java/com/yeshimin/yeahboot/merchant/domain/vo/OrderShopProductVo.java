package com.yeshimin.yeahboot.merchant.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecOptVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商家端订单详情下店铺商品信息VO
 * 数据结构同app端OrderShopProductVo类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderShopProductVo extends BaseDomain {

    /**
     * 订单ID
     */
    @JsonIgnore
    private Long orderId;

    // 以下字段同app端ShopCartItemVo类，直接在此展开

    /**
     * ID
     */
    private Long id;

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
     * spu主图
     */
    private String spuMainImage;

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
