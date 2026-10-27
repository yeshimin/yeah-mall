package com.yeshimin.yeahboot.data.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分订单操作类型
 */
@Getter
@RequiredArgsConstructor
public enum PointsOrderOperationEnum {

    CREATE("创建积分兑换订单"),
    COUPON_AUTO_FULFILL("优惠券自动发放"),
    MEMBER_CANCEL("会员取消"),
    MEMBER_CONFIRM_RECEIPT("会员确认收货"),
    PLATFORM_SHIP("平台发货"),
    PLATFORM_FULFILL("平台人工履约"),
    PLATFORM_CANCEL("平台取消兑换"),
    SYSTEM_AUTO_CONFIRM_RECEIPT("系统自动确认收货");

    private final String description;
}
