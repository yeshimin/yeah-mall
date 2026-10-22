package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 拼团活动状态
 */
@Getter
public enum GroupBuyActivityStatusEnum implements IValueEnum {

    CREATED("1", "新建"),
    PUBLISHED("2", "发布"),
    APPLY_STARTED("3", "开始报名"),
    APPLY_FINISHED("4", "结束报名"),
    ACTIVITY_STARTED("5", "活动开始"),
    ACTIVITY_FINISHED("6", "活动结束"),
    ACTIVITY_DOWN("7", "活动下架");

    private final String value;
    private final String desc;

    GroupBuyActivityStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static GroupBuyActivityStatusEnum of(String value) {
        for (GroupBuyActivityStatusEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
