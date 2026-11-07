package com.yeshimin.yeahboot.notification.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 站内信发送目标类型枚举。
 */
@Getter
public enum NotifMessageTargetTypeEnum implements IValueEnum {

    USER("1", "指定用户"),
    ALL("2", "全部用户");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 枚举描述
     */
    private final String desc;

    NotifMessageTargetTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static NotifMessageTargetTypeEnum of(String value) {
        for (NotifMessageTargetTypeEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
