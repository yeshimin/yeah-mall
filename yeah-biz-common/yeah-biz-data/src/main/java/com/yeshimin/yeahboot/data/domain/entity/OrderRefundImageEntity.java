package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单退款凭证图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order_refund_image")
public class OrderRefundImageEntity extends ShopConditionBaseEntity<OrderRefundImageEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 图片文件键
     */
    private String image;

    /**
     * 排序
     */
    private Integer sort;
}
