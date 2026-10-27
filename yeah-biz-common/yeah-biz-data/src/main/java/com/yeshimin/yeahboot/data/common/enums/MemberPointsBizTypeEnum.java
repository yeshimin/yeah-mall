package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 会员积分业务类型
 */
@Getter
public enum MemberPointsBizTypeEnum implements IValueEnum {

    REGISTER("REGISTER", "注册赠送"),
    ORDER_COMPLETE("ORDER_COMPLETE", "订单完成"),
    ORDER_REFUND("ORDER_REFUND", "订单退款回退"),
    REVIEW("REVIEW", "完成评价"),
    POINTS_EXCHANGE("POINTS_EXCHANGE", "积分兑换"),
    POINTS_CANCEL("POINTS_CANCEL", "积分兑换取消"),
    MANUAL_ADJUST("MANUAL_ADJUST", "平台调整");

    private final String value;
    private final String desc;

    MemberPointsBizTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
