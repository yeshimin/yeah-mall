package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单优惠券表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order_coupon")
public class OrderCouponEntity extends ShopConditionBaseEntity<OrderCouponEntity> {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 会员优惠券ID
     */
    private Long memberCouponId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 优惠券类型：1-满减券 2-折扣券 3-无门槛券
     */
    private Integer type;

    /**
     * 优惠金额
     */
    private BigDecimal amount;

    /**
     * 优惠折扣（计算要乘以100%）
     */
    private BigDecimal discount;

    /**
     * 使用门槛金额
     */
    private BigDecimal minAmount;

    /**
     * 使用范围：1-店铺通用 2-指定商品 3-指定分类
     */
    private Integer useRange;

    /**
     * 目标ID（取决于使用范围）
     */
    private Long targetId;

    /**
     * 有效期起始时间
     */
    private LocalDateTime beginTime;

    /**
     * 有效期截止时间
     */
    private LocalDateTime endTime;
}
