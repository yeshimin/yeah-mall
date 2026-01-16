package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailVo extends BaseDomain {

    /**
     * 主订单信息
     */
    private OrderEntity order;

    /**
     * 店铺商品列表
     */
    private List<OrderShopProductVo> shopProducts;
}
