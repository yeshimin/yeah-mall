package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 会员优惠券查询条件枚举
 */
@Getter
public enum MemberCouponQueryConditionEnum implements IValueEnum {

    AVAILABLE("1", "可用"),
    USED("2", "已使用"),
    EXPIRED("3", "已过期");

    private final String value;
    private final String desc;

    MemberCouponQueryConditionEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static MemberCouponQueryConditionEnum of(String value) {
        for (MemberCouponQueryConditionEnum e : MemberCouponQueryConditionEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
