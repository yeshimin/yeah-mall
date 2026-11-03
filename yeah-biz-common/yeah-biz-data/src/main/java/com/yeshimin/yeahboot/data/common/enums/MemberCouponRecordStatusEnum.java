package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 会员优惠券记录状态
 */
@Getter
public enum MemberCouponRecordStatusEnum implements IValueEnum {

    UNUSED("0", "未使用"),
    USED("1", "已使用"),
    EXPIRED("2", "已过期");

    /**
     * 状态值
     */
    private final String value;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 创建会员优惠券记录状态枚举
     */
    MemberCouponRecordStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
