package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 拼团团状态
 */
@Getter
public enum GroupBuyGroupStatusEnum implements IValueEnum {

    WAIT_PAY("1", "待团长支付"),
    FORMING("2", "拼团中"),
    SUCCESS("3", "已成团"),
    FAILED("4", "拼团失败"),
    CLOSED("5", "已关闭");

    private final String value;
    private final String desc;

    GroupBuyGroupStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static GroupBuyGroupStatusEnum of(String value) {
        for (GroupBuyGroupStatusEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
