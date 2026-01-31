package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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

    // 待评价状态集合
    public static final List<String> WAIT_REVIEW_STATUS = Arrays.asList(
            OrderStatusEnum.COMPLETED.getValue(),
            OrderStatusEnum.REFUND.getValue(),
            OrderStatusEnum.AFTER_SALE.getValue());

    // 退款和售后状态集合
    public static final List<String> REFUND_AND_AFTER_SALE_STATUS = Arrays.asList(
            OrderStatusEnum.REFUND.getValue(),
            OrderStatusEnum.AFTER_SALE.getValue()
    );

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

    /**
     * 判断是否可评价
     */
    public static boolean isReviewable(String status) {
        OrderStatusEnum statusEnum = of(status);
        if (statusEnum == null) {
            throw new IllegalArgumentException("订单状态错误");
        }

        return WAIT_REVIEW_STATUS.contains(status);
    }
}
