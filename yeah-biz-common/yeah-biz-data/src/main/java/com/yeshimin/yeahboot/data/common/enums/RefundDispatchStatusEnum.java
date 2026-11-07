package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 退款请求调度状态。
 */
@Getter
public enum RefundDispatchStatusEnum implements IValueEnum {

    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    SUBMITTED("SUBMITTED", "已提交微信"),
    RETRY("RETRY", "等待重试"),
    FAILED("FAILED", "已停止"),
    COMPLETED("COMPLETED", "已结束");

    private final String value;
    private final String desc;

    RefundDispatchStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
