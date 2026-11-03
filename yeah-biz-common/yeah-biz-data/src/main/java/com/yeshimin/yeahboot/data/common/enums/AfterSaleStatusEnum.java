package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单售后状态
 */
@Getter
public enum AfterSaleStatusEnum implements IValueEnum {

    NONE("0", "无"),
    APPLYING("1", "申请中"),
    PROCESSING("2", "处理中"),
    COMPLETED("3", "售后完成"),
    REJECTED("4", "已驳回");

    /**
     * 状态值
     */
    private final String value;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 创建订单售后状态枚举
     */
    AfterSaleStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
