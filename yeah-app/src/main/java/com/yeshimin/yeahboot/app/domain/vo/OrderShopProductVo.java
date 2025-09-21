package com.yeshimin.yeahboot.app.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单下店铺商品信息VO
 * 数据结构同【购物车商品信息VO】
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderShopProductVo extends ShopCartItemVo {
}
