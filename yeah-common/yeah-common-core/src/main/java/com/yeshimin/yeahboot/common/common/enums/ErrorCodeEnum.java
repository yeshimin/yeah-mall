package com.yeshimin.yeahboot.common.common.enums;

/**
 * 错误码枚举
 */
public enum ErrorCodeEnum {

    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
    ;

    private final Integer code;

    private final String desc;

    ErrorCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
