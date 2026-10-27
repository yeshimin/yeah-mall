package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分订单状态日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_points_order_log")
public class PointsOrderLogEntity extends ConditionBaseEntity<PointsOrderLogEntity> {

    /**
     * 积分订单ID
     */
    private Long orderId;

    /**
     * 积分订单编号
     */
    private String orderNo;

    /**
     * 原状态
     */
    private String statusFrom;

    /**
     * 新状态
     */
    private String statusTo;

    /**
     * 操作说明
     */
    private String remark;
}
