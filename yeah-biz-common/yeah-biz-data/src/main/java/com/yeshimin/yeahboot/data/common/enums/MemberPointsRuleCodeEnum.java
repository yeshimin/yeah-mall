package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 会员积分规则编码
 */
@Getter
public enum MemberPointsRuleCodeEnum implements IValueEnum {

    REGISTER("REGISTER", "注册赠送"),
    ORDER_COMPLETE("ORDER_COMPLETE", "订单完成"),
    REVIEW("REVIEW", "完成评价");

    private final String value;
    private final String desc;

    MemberPointsRuleCodeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
