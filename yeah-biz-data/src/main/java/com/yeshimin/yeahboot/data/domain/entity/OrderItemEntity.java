package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单明细表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order_item")
public class OrderItemEntity extends ShopConditionBaseEntity<OrderItemEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号（冗余）
     */
    private String orderNo;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU name（快照）
     */
    private String skuName;

    /**
     * SPU ID（快照）
     */
    private Long spuId;

    /**
     * SPU name（快照）
     */
    private String spuName;

    /**
     * 商品单价
     */
    private BigDecimal unitPrice;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品总价
     */
    private BigDecimal totalPrice;
}
