package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单退款状态枚举
 */
@Getter
public enum RefundStatusEnum implements IValueEnum {

    NONE("0", "无"),
    APPLYING("1", "申请中"),
    PROCESSING("2", "处理中"),
    SUCCESS("3", "退款成功"),
    REJECTED("4", "已拒绝"),
    FAILED("5", "退款失败");

    private final String value;
    private final String desc;

    RefundStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static RefundStatusEnum of(String value) {
        for (RefundStatusEnum e : RefundStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
