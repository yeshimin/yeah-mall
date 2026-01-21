package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单物流跟踪状态枚举
 * https://www.juhe.cn/docs/api/id/43
 */
@Getter
public enum OrderDeliveryTrackingStatusEnum implements IValueEnum {

    PENDING("PENDING", "待查询"),
    NO_RECORD("NO_RECORD", "无记录"),
    ERROR("ERROR", "查询异常"),
    IN_TRANSIT("IN_TRANSIT", "运输中"),
    DELIVERING("DELIVERING", "派送中"),
    SIGNED("SIGNED", "已签收"),
    REJECTED("REJECTED", "拒签"),
    PROBLEM("PROBLEM", "疑难件"),
    INVALID("INVALID", "无效件"),
    TIMEOUT("TIMEOUT", "超时件"),
    FAILED("FAILED", "派送失败"),
    SEND_BACK("SEND_BACK", "退回"),
    TAKING("TAKING", "揽件");

    private final String value;
    private final String desc;

    OrderDeliveryTrackingStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OrderDeliveryTrackingStatusEnum of(String value) {
        for (OrderDeliveryTrackingStatusEnum e : OrderDeliveryTrackingStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
