package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 会员积分规则计算方式
 */
@Getter
public enum MemberPointsRuleCalcTypeEnum implements IValueEnum {

    FIXED("1", "固定积分"),
    AMOUNT_RATIO("2", "按金额比例");

    private final String value;
    private final String desc;

    MemberPointsRuleCalcTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
