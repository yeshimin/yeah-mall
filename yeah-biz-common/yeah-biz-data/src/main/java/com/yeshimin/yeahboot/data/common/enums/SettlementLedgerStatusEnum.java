package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 商家结算台账状态
 */
@Getter
public enum SettlementLedgerStatusEnum implements IValueEnum {

    PENDING("1", "待结算"),
    SETTLED("2", "已结算"),
    CANCELED("3", "已取消");

    private final String value;
    private final String desc;

    SettlementLedgerStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
