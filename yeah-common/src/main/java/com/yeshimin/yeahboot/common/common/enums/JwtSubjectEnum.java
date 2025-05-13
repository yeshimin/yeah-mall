package com.yeshimin.yeahboot.common.common.enums;

/**
 * Jwt主题：区分（子）系统
 */
public enum JwtSubjectEnum {

    // default-默认
    DEFAULT("default", "默认"),
    ;

    private final String value;
    private final String desc;

    JwtSubjectEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static JwtSubjectEnum of(String value) {
        for (JwtSubjectEnum e : JwtSubjectEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
