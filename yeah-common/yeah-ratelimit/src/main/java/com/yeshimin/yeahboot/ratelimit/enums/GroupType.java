package com.yeshimin.yeahboot.ratelimit.enums;

/**
 * 分组方式：1-无 2-按IP 3-按用户
 */
public enum GroupType {

    DEFAULT(1),
    IP(2),
    USER(3),
    CUSTOM(4);

    private final Integer value;

    GroupType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
