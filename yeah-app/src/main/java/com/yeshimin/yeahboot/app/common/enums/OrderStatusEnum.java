package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatusEnum implements IValueEnum {

    WAIT_PAY("1", "待付款"),
    WAIT_SHIP("2", "待发货"),
    WAIT_RECEIVE("3", "待收货"),
    COMPLETED("4", "交易成功"),
    CLOSED("5", "交易关闭"),
    REFUND("6", "退款"),
    AFTER_SALE("7", "售后");

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
