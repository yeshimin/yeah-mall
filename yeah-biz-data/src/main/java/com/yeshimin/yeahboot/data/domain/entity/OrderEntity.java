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
     * 状态：1-待付款 2-已付款 3-待发货 4-已发货 5-已收货 6-已完成 7-已取消 8-已退款
     */
    private String status;
}
