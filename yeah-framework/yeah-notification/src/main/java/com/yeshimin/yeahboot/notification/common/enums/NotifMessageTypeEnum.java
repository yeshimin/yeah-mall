package com.yeshimin.yeahboot.notification.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 站内信消息类型枚举。
 */
@Getter
public enum NotifMessageTypeEnum implements IValueEnum {

    SYSTEM("1", "系统消息"),
    BUSINESS("2", "业务消息"),
    ANNOUNCEMENT("3", "通告");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 枚举描述
     */
    private final String desc;

    NotifMessageTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static NotifMessageTypeEnum of(String value) {
        for (NotifMessageTypeEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
