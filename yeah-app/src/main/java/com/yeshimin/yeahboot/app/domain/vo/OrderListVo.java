package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.vo.OrderShopProductVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * App订单列表视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderListVo extends BaseDomain {

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
    private List<OrderShopProductVo> items;

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
     * 退款状态
     */
    private String refundStatus;

    /**
     * 售后状态
     */
    private String afterSaleStatus;

    /**
     * 是否已评价
     */
    private Boolean isReviewed;

    /**
     * 商品总额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 优惠后业务应付金额
     */
    private BigDecimal finalAmount;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

    /**
     * 支付成功时间
     */
    private LocalDateTime paySuccessTime;
}
