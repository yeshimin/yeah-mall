package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order")
public class OrderEntity extends ShopConditionBaseEntity<OrderEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态：1-待付款 2-待发货 3-待收货 4-交易成功 5-交易关闭 6-退款 7-售后
     */
    private String status;

    /**
     * 退款状态：0-无 1-申请中 2-处理中 3-退款成功 4-已拒绝
     */
    private String refundStatus;

    /**
     * 售后状态：0-无 1-申请中 2-处理中 3-售后完成 4-已驳回
     */
    private String afterSaleStatus;

    /**
     * 是否已评价
     */
    private Boolean reviewed;

    /**
     * 微信预支付ID
     */
    private String wxPrepayId;
}
