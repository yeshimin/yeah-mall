package com.yeshimin.yeahboot.common.common.exception;

import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * 基类异常
 */
@Getter
public class BaseException extends RuntimeException {

    // 错误码
    private final ErrorCodeEnum errorCode;

    public BaseException() {
        this(ErrorCodeEnum.FAIL);
    }

    public BaseException(ErrorCodeEnum errorCode) {
        this(errorCode, errorCode.getDesc());
    }

    public BaseException(String message) {
        this(ErrorCodeEnum.FAIL, message);
    }

    public BaseException(ErrorCodeEnum errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}