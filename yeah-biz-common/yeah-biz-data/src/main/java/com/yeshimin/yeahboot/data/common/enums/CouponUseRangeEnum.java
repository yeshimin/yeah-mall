package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 优惠券使用范围枚举
 */
@Getter
public enum CouponUseRangeEnum implements IValueEnum {

    SHOP("1", "店铺通用"),
    PRODUCT("2", "指定商品"),
    CATEGORY("3", "指定分类");

    private final String value;
    private final String desc;

    CouponUseRangeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static CouponUseRangeEnum of(String value) {
        for (CouponUseRangeEnum e : CouponUseRangeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
