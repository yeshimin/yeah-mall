package com.yeshimin.yeahboot.ratelimit.enums;

import lombok.Getter;

/**
 * 分组方式：1-无 2-按IP 3-按用户
 */
@Getter
public enum GroupType {

    NONE(1),
    IP(2),
    CUSTOM(3);

    private final Integer value;

    GroupType(Integer value) {
        this.value = value;
    }

}
