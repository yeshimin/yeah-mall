package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 客服聊天消息类型枚举
 */
@Getter
public enum CsMsgTypeEnum implements IValueEnum {

    TEXT("1", "文本"),
    IMAGE("2", "图片");

    private final String value;
    private final String desc;

    CsMsgTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static CsMsgTypeEnum of(String value) {
        for (CsMsgTypeEnum e : CsMsgTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
