//package com.yeshimin.yeahboot.app.domain.vo;
//
//import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
///**
// * 微信小程序支付信息VO
// * 字段说明：https://pay.weixin.qq.com/doc/v3/merchant/4012791898
// */
//@Data
//@EqualsAndHashCode(callSuper = true)
//@Schema(description = "App端-微信支付-预支付结果VO")
//public class WxPayInfoVo extends BaseDomain {
//
//    /**
//     * 时间戳
//     */
//    @Schema(description = "时间戳")
//    private String timestamp;
//
//    /**
//     * 随机字符串
//     */
//    @Schema(description = "随机字符串")
//    private String nonceStr;
//
//    /**
//     * 预支付交易会话标识
//     */
//    @Schema(description = "预支付交易会话标识")
//    private String packageStr;
//
//    /**
//     * 签名类型
//     */
//    @Schema(description = "签名类型")
//    private String signType;
//
//    /**
//     * 签名值
//     */
//    @Schema(description = "签名值")
//    private String paySign;
//}
