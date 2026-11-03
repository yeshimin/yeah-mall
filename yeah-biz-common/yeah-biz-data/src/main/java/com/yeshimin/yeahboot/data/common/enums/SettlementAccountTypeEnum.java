package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 商家结算账户类型
 */
@Getter
public enum SettlementAccountTypeEnum implements IValueEnum {

    BANK_CARD("1", "银行卡"),
    ALIPAY("2", "支付宝");

    /**
     * 类型值
     */
    private final String value;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 创建商家结算账户类型枚举
     */
    SettlementAccountTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
