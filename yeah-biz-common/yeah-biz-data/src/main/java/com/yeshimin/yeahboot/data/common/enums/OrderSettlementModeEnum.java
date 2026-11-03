package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单结算模式
 */
@Getter
public enum OrderSettlementModeEnum implements IValueEnum {

    MANUAL("1", "平台线下结算"),
    WECHAT_PROFIT_SHARING("2", "微信分账"),
    NONE("3", "无需二次结算");

    private final String value;
    private final String desc;

    OrderSettlementModeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
