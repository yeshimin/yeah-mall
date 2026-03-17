package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券列表项VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponVo extends BaseDomain {

    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 优惠券类型：1-满减券 2-折扣券 3-无门槛券
     */
    private Integer type;

    /**
     * 优惠金额
     */
    private BigDecimal amount;

    /**
     * 优惠折扣（计算要乘以100%）
     */
    private BigDecimal discount;

    /**
     * 使用门槛金额
     */
    private BigDecimal minAmount;

    /**
     * 总数量
     */
    private Integer quantity;

    /**
     * 已领取数量
     */
    private Integer received;

    /**
     * 已使用数量
     */
    private Integer used;

    /**
     * 每人限领数量
     */
    private Integer perLimit;

    /**
     * 使用范围：1-店铺通用 2-指定商品 3-指定分类
     */
    private Integer useRange;

    /**
     * 目标ID（取决于使用范围）
     */
    private Long targetId;

    /**
     * 有效期起始时间
     */
    private LocalDateTime beginTime;

    /**
     * 有效期截止时间
     */
    private LocalDateTime endTime;

    /**
     * 排序：大于等于1
     */
    private Integer sort;

    /**
     * 当前用户已领取数量
     */
    private Integer receivedCount;
}
