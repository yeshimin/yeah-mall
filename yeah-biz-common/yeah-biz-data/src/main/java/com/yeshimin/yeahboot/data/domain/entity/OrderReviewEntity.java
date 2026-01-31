package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单评价表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order_review")
public class OrderReviewEntity extends ShopConditionBaseEntity<OrderReviewEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 描述相符：1-5
     */
    private Integer descriptionRating;

    /**
     * 物流服务：1-5
     */
    private Integer deliveryRating;

    /**
     * 服务态度：1-5
     */
    private Integer serviceRating;
}
