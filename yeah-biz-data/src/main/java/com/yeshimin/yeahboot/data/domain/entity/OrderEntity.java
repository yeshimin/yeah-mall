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
}
