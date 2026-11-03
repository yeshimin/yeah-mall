package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 商城App运行模式
 */
@Getter
public enum MallAppModeEnum implements IValueEnum {

    PLATFORM("PLATFORM", "平台商城"),
    SHOP("SHOP", "独立店铺");

    /**
     * 模式值
     */
    private final String value;

    /**
     * 模式描述
     */
    private final String desc;

    /**
     * 创建商城App运行模式枚举
     */
    MallAppModeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
