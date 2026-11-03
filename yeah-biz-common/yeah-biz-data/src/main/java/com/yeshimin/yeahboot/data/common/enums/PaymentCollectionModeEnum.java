package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 支付收款模式
 */
@Getter
public enum PaymentCollectionModeEnum implements IValueEnum {

    PLATFORM_DIRECT("1", "平台普通商户直收"),
    PLATFORM_PARTNER("2", "服务商代子商户收款"),
    SHOP_DIRECT("3", "独立店铺普通商户直收");

    private final String value;
    private final String desc;

    PaymentCollectionModeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
