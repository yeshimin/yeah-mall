package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 客服聊天消息方向枚举
 */
@Getter
public enum CsMsgDirectionEnum implements IValueEnum {

    MEM2MCH("1", "买家到商家"),
    MCH2MEM("2", "商家到买家");

    private final String value;
    private final String desc;

    CsMsgDirectionEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static CsMsgDirectionEnum of(String value) {
        for (CsMsgDirectionEnum e : CsMsgDirectionEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
