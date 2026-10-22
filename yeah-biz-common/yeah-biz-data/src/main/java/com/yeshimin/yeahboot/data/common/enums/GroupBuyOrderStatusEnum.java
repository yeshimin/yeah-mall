package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 拼团订单参与状态
 */
@Getter
public enum GroupBuyOrderStatusEnum implements IValueEnum {

    WAIT_PAY("1", "待支付"),
    PAID("2", "已支付"),
    CLOSED("3", "已关闭"),
    REFUNDING("4", "退款中"),
    REFUNDED("5", "已退款"),
    REFUND_FAILED("6", "退款失败");

    private final String value;
    private final String desc;

    GroupBuyOrderStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static GroupBuyOrderStatusEnum of(String value) {
        for (GroupBuyOrderStatusEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
