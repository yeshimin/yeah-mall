package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单下店铺信息VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderShopVo extends BaseDomain {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商品项
     */
    List<OrderShopProductVo> items;
}
