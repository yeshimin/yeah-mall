package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 商家结算台账业务类型
 */
@Getter
public enum SettlementLedgerBizTypeEnum implements IValueEnum {

    ORDER("1", "订单收入"),
    REFUND("2", "退款冲减"),
    ADJUSTMENT("3", "人工调整");

    private final String value;
    private final String desc;

    SettlementLedgerBizTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
