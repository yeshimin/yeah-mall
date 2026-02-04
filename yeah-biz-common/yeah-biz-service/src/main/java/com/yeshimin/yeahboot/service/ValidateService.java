package com.yeshimin.yeahboot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final Validator validator;

    /**
     * 检查对象是否有校验错误
     */
    public boolean hasError(Object obj) {
        return obj == null || !validator.validate(obj).isEmpty();
    }
}
