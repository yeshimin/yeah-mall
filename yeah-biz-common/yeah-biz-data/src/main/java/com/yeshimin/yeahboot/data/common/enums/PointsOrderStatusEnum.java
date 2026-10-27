package com.yeshimin.yeahboot.data.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分订单状态
 */
@Getter
@RequiredArgsConstructor
public enum PointsOrderStatusEnum {

    PENDING_FULFILLMENT("1", "待履约"),
    SHIPPED("2", "已发货"),
    COMPLETED("3", "已完成"),
    CANCELED("4", "已取消");

    private final String value;
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static PointsOrderStatusEnum of(String value) {
        for (PointsOrderStatusEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
