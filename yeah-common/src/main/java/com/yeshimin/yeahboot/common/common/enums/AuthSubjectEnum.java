package com.yeshimin.yeahboot.common.common.enums;

import lombok.Getter;

/**
 * 授权相关-主题（子系统/模块）枚举
 */
@Getter
public enum AuthSubjectEnum {

    // admin
    ADMIN("admin", "管理端"),
    // app
    APP("app", "App端"),
    // merchant
    MERCHANT("merchant", "商家端");

    private final String value;
    private final String desc;

    AuthSubjectEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static AuthSubjectEnum of(String value) {
        for (AuthSubjectEnum e : AuthSubjectEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
