package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 订单退款记录退款状态枚举，同微信退款状态
 * https://pay.weixin.qq.com/doc/v3/merchant/4012791906
 */
@Getter
public enum WxRefundStatusEnum implements IValueEnum {

    SUCCESS("SUCCESS", "退款成功"),
    CLOSED("CLOSED", "退款关闭"),
    PROCESSING("PROCESSING", "退款处理中"),
    ABNORMAL("ABNORMAL", "退款异常");

    private final String value;
    private final String desc;

    WxRefundStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static WxRefundStatusEnum of(String value) {
        for (WxRefundStatusEnum e : WxRefundStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
