package com.yeshimin.yeahboot.data.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分商品类型
 */
@Getter
@RequiredArgsConstructor
public enum PointsProductTypeEnum {

    PHYSICAL(1, "实物商品"),
    COUPON(2, "优惠券"),
    BENEFIT(3, "虚拟权益");

    private final Integer value;
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static PointsProductTypeEnum of(Integer value) {
        for (PointsProductTypeEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
