package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 秒杀活动状态枚举
 */
@Getter
public enum SeckillActivityStatusEnum implements IValueEnum {

    CREATED("1", "新建"),
    PUBLISHED("2", "发布"),
    START_APPLY("3", "开始报名"),
    END_APPLY("4", "结束报名"),
    START_ACTIVITY("5", "开始活动"),
    END_ACTIVITY("6", "结束活动");

    private final String value;
    private final String desc;

    SeckillActivityStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static SeckillActivityStatusEnum of(String value) {
        for (SeckillActivityStatusEnum e : SeckillActivityStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 发布后的状态集合
     */
    public static final List<String> PUBLISHED_STATUS = Arrays.asList(
            SeckillActivityStatusEnum.PUBLISHED.getValue(),
            SeckillActivityStatusEnum.START_APPLY.getValue(),
            SeckillActivityStatusEnum.END_APPLY.getValue(),
            SeckillActivityStatusEnum.START_ACTIVITY.getValue(),
            SeckillActivityStatusEnum.END_ACTIVITY.getValue());

    /**
     * 是否已发布（及之后状态）
     */
    public static boolean isPublished(String value) {
        return PUBLISHED_STATUS.contains(value);
    }
}
