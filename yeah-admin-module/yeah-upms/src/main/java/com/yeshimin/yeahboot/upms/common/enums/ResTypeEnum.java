package com.yeshimin.yeahboot.upms.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 系统资源类型枚举
 */
@Getter
public enum ResTypeEnum implements IValueEnum {

    // 类型：1-菜单 2-页面 3-按钮 4-接口
    MENU("1", "菜单"),
    PAGE("2", "页面"),
    BUTTON("3", "按钮"),
    API("4", "接口");

    private final String value;
    private final String desc;

    ResTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ResTypeEnum of(String value) {
        for (ResTypeEnum e : ResTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
