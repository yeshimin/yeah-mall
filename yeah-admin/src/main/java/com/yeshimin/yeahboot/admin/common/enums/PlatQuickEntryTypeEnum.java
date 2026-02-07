package com.yeshimin.yeahboot.admin.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 平台快捷入口类型枚举
 */
@Getter
public enum PlatQuickEntryTypeEnum implements IValueEnum {

    TYPE_1("1", "业务1"),
    TYPE_2("2", "业务2");

    private final String value;
    private final String desc;

    PlatQuickEntryTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static PlatQuickEntryTypeEnum of(String value) {
        for (PlatQuickEntryTypeEnum e : PlatQuickEntryTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
