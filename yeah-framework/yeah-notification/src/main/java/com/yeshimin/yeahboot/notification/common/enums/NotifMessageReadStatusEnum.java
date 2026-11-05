package com.yeshimin.yeahboot.notification.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 站内信阅读状态枚举。
 */
@Getter
public enum NotifMessageReadStatusEnum implements IValueEnum {

    UNREAD("0", "未读"),
    READ("1", "已读");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 枚举描述
     */
    private final String desc;

    NotifMessageReadStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static NotifMessageReadStatusEnum of(String value) {
        for (NotifMessageReadStatusEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
