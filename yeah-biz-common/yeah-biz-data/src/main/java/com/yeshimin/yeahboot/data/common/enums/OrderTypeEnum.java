package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单类型枚举
 */
@Getter
public enum OrderTypeEnum implements IValueEnum {

    NORMAL("1", "普通订单"),
    SECKILL("2", "秒杀订单");

    private final String value;
    private final String desc;

    OrderTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OrderTypeEnum of(String value) {
        for (OrderTypeEnum e : OrderTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
