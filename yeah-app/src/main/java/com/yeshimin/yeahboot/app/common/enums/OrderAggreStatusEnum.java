package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import lombok.Getter;

/**
 * 订单聚合状态枚举
 */
@Getter
public enum OrderAggreStatusEnum implements IValueEnum {

    WAIT_PAY("1", "待付款"),
    WAIT_SHIP("2", "待发货"),
    WAIT_RECEIVE("3", "待收货"),
    REFUND_AND_AFTER_SALE("4", "退款/售后"),
    REVIEW("5", "评价");

    private final String value;
    private final String desc;

    OrderAggreStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OrderAggreStatusEnum of(String value) {
        for (OrderAggreStatusEnum e : OrderAggreStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    public boolean isEqualMode() {
        return this.value.equals(WAIT_PAY.getValue()) ||
                this.value.equals(WAIT_SHIP.getValue()) ||
                this.value.equals(WAIT_RECEIVE.getValue());
    }

    /**
     * 配合isEqualMode=true的情况使用
     */
    public OrderStatusEnum toOrderStatusEnum() {
        switch (this) {
            case WAIT_PAY:
                return OrderStatusEnum.WAIT_PAY;
            case WAIT_SHIP:
                return OrderStatusEnum.WAIT_SHIP;
            case WAIT_RECEIVE:
                return OrderStatusEnum.WAIT_RECEIVE;
            default:
                throw new IllegalArgumentException("Cannot convert to OrderStatusEnum: " + this);
        }
    }

    public boolean isInMode() {
        return this.value.equals(REFUND_AND_AFTER_SALE.getValue());
    }

    public boolean isExtendMode() {
        return this.value.equals(REVIEW.getValue());
    }
}
