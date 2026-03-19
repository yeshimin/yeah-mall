package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.data.common.enums.CouponTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.CouponUseRangeEnum;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class MchCouponCreateDto extends ShopDataBaseDomain {

    /**
     * 优惠券名称
     */
    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 优惠券类型：1-满减券 2-折扣券 3-无门槛券
     */
    @NotNull(message = "优惠券类型不能为空")
    @EnumValue(enumClass = CouponTypeEnum.class)
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
    @Min(value = 0, message = "使用门槛金额不能小于0")
    @NotNull(message = "使用门槛金额不能为空")
    private BigDecimal minAmount;

    /**
     * 总数量
     */
    @Min(value = 1, message = "总数量不能小于1")
    @NotNull(message = "总数量不能为空")
    private Integer quantity;

    /**
     * 每人限领数量
     */
    @Min(value = 1, message = "每人限领数量不能小于1")
    @NotNull(message = "每人限领数量不能为空")
    private Integer perLimit;

    /**
     * 使用范围：1-店铺通用 2-指定商品 3-指定分类
     */
    @NotNull(message = "使用范围不能为空")
    @EnumValue(enumClass = CouponUseRangeEnum.class)
    private Integer useRange;

    /**
     * 目标ID（取决于使用范围）
     */
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    /**
     * 有效期起始时间
     */
    @NotNull(message = "有效期起始时间不能为空")
    private LocalDateTime beginTime;

    /**
     * 有效期截止时间
     */
    @NotNull(message = "有效期截止时间不能为空")
    private LocalDateTime endTime;

    /**
     * 是否启用
     */
    @NotNull(message = "是否启用不能为空")
    private Boolean isEnabled;

    /**
     * 备注
     */
    private String remark;
}
