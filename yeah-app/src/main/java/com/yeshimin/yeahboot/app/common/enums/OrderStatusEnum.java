package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatusEnum implements IValueEnum {

    WAIT_PAY("1", "待付款"),
    PAID("2", "已付款"),
    WAIT_SHIP("3", "待发货"),
    SHIPPED("4", "已发货"),
    RECEIVED("5", "已收货"),
    COMPLETED("6", "已完成"),
    CANCELED("7", "已取消"),
    REFUNDED("8", "已退款");

    private final String value;
    private final String desc;

    OrderStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OrderStatusEnum of(String value) {
        for (OrderStatusEnum e : OrderStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
