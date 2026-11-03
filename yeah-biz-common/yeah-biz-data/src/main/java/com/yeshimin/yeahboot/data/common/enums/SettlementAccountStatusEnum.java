package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 商家结算账户审核状态
 */
@Getter
public enum SettlementAccountStatusEnum implements IValueEnum {

    PENDING("1", "待审核"),
    APPROVED("2", "已通过"),
    REJECTED("3", "已拒绝");

    private final String value;
    private final String desc;

    SettlementAccountStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
