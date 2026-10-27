package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 会员优惠券领取渠道
 */
@Getter
public enum MemberCouponReceiveChannelEnum implements IValueEnum {

    NORMAL_RECEIVE("1", "普通领取"),
    POINTS_EXCHANGE("2", "积分兑换");

    private final String value;
    private final String desc;

    MemberCouponReceiveChannelEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据数据库值获取枚举
     */
    public static MemberCouponReceiveChannelEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        for (MemberCouponReceiveChannelEnum item : values()) {
            if (item.equalsValue(value)) {
                return item;
            }
        }
        return null;
    }
}
