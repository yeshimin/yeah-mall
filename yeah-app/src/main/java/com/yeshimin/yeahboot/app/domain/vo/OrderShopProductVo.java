package com.yeshimin.yeahboot.app.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单下店铺商品信息VO
 * 用户订单列表页-店铺商品项
 * 数据结构同【购物车商品信息VO】
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderShopProductVo extends ShopCartItemVo {

    /**
     * 订单ID
     */
    @JsonIgnore
    private Long orderId;
}
