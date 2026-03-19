package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 优惠券类型枚举
 */
@Getter
public enum CouponTypeEnum implements IValueEnum {

    FULL_REDUCTION("1", "满减券"),
    DISCOUNT("2", "折扣券"),
    NO_THRESHOLD("3", "无门槛券");

    private final String value;
    private final String desc;

    CouponTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static CouponTypeEnum of(String value) {
        for (CouponTypeEnum e : CouponTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    // of Integer value
    public static CouponTypeEnum of(Integer value) {
        for (CouponTypeEnum e : CouponTypeEnum.values()) {
            if (e.getValue().equals(String.valueOf(value))) {
                return e;
            }
        }
        return null;
    }
}
