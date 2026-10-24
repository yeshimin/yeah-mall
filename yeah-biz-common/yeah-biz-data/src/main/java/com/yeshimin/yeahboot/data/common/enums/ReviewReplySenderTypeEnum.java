package com.yeshimin.yeahboot.data.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 评价回复方类型
 */
@Getter
@RequiredArgsConstructor
public enum ReviewReplySenderTypeEnum {

    /**
     * 买家
     */
    MEMBER(1),

    /**
     * 商家
     */
    MERCHANT(2);

    private final int value;
}
