package com.yeshimin.yeahboot.common.common.enums;

import lombok.Getter;

/**
 * 授权相关-终端枚举
 */
@Getter
public enum AuthTerminalEnum {

    // web
    WEB("web", "web端"),
    // app
    APP("app", "app端");

    private final String value;
    private final String desc;

    AuthTerminalEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static AuthTerminalEnum of(String value) {
        for (AuthTerminalEnum e : AuthTerminalEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
