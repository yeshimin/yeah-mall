package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单场景枚举
 */
@Getter
public enum OrderSceneEnum implements IValueEnum {

    PRODUCT("1", "商品页下单"),
    CART("2", "购物车下单");

    private final String value;
    private final String desc;

    OrderSceneEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OrderSceneEnum of(String value) {
        for (OrderSceneEnum e : OrderSceneEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
