package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单物流跟踪表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order_delivery_tracking")
public class OrderDeliveryTrackingEntity extends ConditionBaseEntity<OrderDeliveryTrackingEntity> {

    /**
     * 商户ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号（冗余）
     */
    private String orderNo;

    /**
     * 快递单号
     */
    private String trackingNo;

    /**
     * 物流状态
     */
    private String trackingStatus;

    /**
     * 上次查询的原始响应数据
     */
    private String lastRespData;

    /**
     * 上次查询时间
     */
    private LocalDateTime lastQueryTime;
}
