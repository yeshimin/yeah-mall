package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 评价等级筛选类型
 */
@Getter
public enum ReviewRatingTypeEnum implements IValueEnum {

    GOOD("good", "好评"),
    MIDDLE("middle", "中评"),
    BAD("bad", "差评");

    /**
     * 类型值
     */
    private final String value;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 创建评价等级筛选类型枚举
     */
    ReviewRatingTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
