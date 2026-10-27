package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 用户反馈处理状态
 */
@Getter
public enum FeedbackStatusEnum implements IValueEnum {

    PENDING("1", "待处理"),
    PROCESSING("2", "处理中"),
    RESOLVED("3", "已解决");

    private final String value;
    private final String desc;

    FeedbackStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static FeedbackStatusEnum of(String value) {
        for (FeedbackStatusEnum status : FeedbackStatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
