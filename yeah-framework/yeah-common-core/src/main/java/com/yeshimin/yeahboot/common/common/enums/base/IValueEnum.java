package com.yeshimin.yeahboot.common.common.enums.base;

public interface IValueEnum {

    String getValue();

    default Integer getIntValue() {
        return Integer.parseInt(getValue());
    }
}
