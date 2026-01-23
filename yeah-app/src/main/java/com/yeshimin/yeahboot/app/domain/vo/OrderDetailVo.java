package com.yeshimin.yeahboot.app.domain.vo;

import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.vo.OrderShopProductVo;
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
    private OrderVo order;

    /**
     * 店铺商品列表
     */
    private List<OrderShopProductVo> shopProducts;

    /**
     * 物流跟踪信息
     */
    private JSONObject deliveryTracking;
}
