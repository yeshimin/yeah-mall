package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 微信支付订单状态枚举
 * https://pay.weixin.qq.com/doc/v3/merchant/4012791861
 */
@Getter
public enum WxPayOrderStatusEnum implements IValueEnum {

    SUCCESS("SUCCESS", "支付成功"),
    REFUND("REFUND", "转入退款"),
    NOTPAY("NOTPAY", "未支付"),
    CLOSED("CLOSED", "已关闭"),
    REVOKED("REVOKED", "已撤销"), // 付款码场景
    USERPAYING("USERPAYING", "用户支付中"), // 付款码场景
    PAYERROR("PAYERROR", "支付失败"); // 付款码场景

    private final String value;
    private final String desc;

    WxPayOrderStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static WxPayOrderStatusEnum of(String value) {
        for (WxPayOrderStatusEnum e : WxPayOrderStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
