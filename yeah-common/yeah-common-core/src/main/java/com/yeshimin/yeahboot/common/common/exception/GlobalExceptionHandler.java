package com.yeshimin.yeahboot.common.common.exception;

import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理兜底异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error(String.format("handleException(), e.msg: %s", e.getMessage()), e);
        return R.fail(e.getMessage());
    }

    /**
     * 处理基类异常
     */
    @ExceptionHandler(BaseException.class)
    public R<Void> handleBaseException(BaseException e) {
        log.error(String.format("handleBaseException(), e.code: %s, e.msg: %s",
                e.getErrorCode().getCode(), e.getMessage()), e);
        return R.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler({
            ConversionFailedException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            NoHandlerFoundException.class,
            BindException.class})
    public R<Void> handleParamException(Exception e) {
        String msg = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            msg = Optional.ofNullable(ex.getBindingResult().getFieldError())
                    .map(FieldError::getDefaultMessage)
                    .orElse("参数校验失败");
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            msg = Optional.ofNullable(ex.getBindingResult().getFieldError())
                    .map(FieldError::getDefaultMessage)
                    .orElse("参数校验失败");
        } else if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException ex = (MissingServletRequestParameterException) e;
            msg = "缺少参数: " + ex.getParameterName();
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException ex = (MethodArgumentTypeMismatchException) e;
            msg = "参数类型错误: " + ex.getName();
        }
        log.error(String.format("handleParamException(), e.msg: %s", msg), e);
        return R.fail(msg);
    }
}