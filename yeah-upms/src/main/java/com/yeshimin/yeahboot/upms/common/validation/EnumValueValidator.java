package com.yeshimin.yeahboot.upms.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * 枚举值校验器
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Integer> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants)
                .anyMatch(enumConstant -> enumConstant.ordinal() + 1 == value);
    }
}
