package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 拼团报名审核状态
 */
@Getter
public enum GroupBuyApplyAuditStatusEnum implements IValueEnum {

    PENDING("1", "待审核"),
    PASSED("2", "审核通过"),
    REJECTED("3", "审核驳回");

    private final String value;
    private final String desc;

    GroupBuyApplyAuditStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static GroupBuyApplyAuditStatusEnum of(String value) {
        for (GroupBuyApplyAuditStatusEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
