package com.yeshimin.yeahboot.data.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分商品履约方式
 */
@Getter
@RequiredArgsConstructor
public enum PointsFulfillmentTypeEnum {

    LOGISTICS(1, "物流发货"),
    AUTO_COUPON(2, "自动发券"),
    MANUAL(3, "人工履约");

    private final Integer value;
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static PointsFulfillmentTypeEnum of(Integer value) {
        for (PointsFulfillmentTypeEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
