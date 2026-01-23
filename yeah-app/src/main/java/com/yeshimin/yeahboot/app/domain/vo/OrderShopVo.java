package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.vo.OrderShopProductVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
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

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付成功时间
     */
    private LocalDateTime paySuccessTime;
}
