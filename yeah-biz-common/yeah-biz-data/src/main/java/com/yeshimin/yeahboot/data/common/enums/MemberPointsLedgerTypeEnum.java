package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 会员积分流水类型
 */
@Getter
public enum MemberPointsLedgerTypeEnum implements IValueEnum {

    EARN("1", "获得"),
    CONSUME("2", "使用"),
    ADJUST("3", "调整");

    private final String value;
    private final String desc;

    MemberPointsLedgerTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
