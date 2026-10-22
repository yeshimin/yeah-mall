package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家优惠券领取和使用记录视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchCouponRecordVo extends BaseDomain {

    /**
     * 会员优惠券ID
     */
    private Long id;

    /**
     * 商家优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型
     */
    private Integer couponType;

    /**
     * 优惠券金额或折扣
     */
    private BigDecimal couponValue;

    /**
     * 最低使用金额
     */
    private BigDecimal minAmount;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 使用订单编号
     */
    private String orderNo;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 优惠券状态
     */
    private Integer status;
}
