package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 店铺创建参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopCreateDto extends BaseDomain {

    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Long mchId;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 平台是否启用
     */
    private Boolean isEnabled;

    /**
     * 平台结算佣金比例，单位百分比
     */
    @DecimalMin(value = "0", message = "结算佣金比例不能小于0")
    @DecimalMax(value = "100", message = "结算佣金比例不能大于100")
    private BigDecimal settlementCommissionRate;

    /**
     * 订单完成后延迟结算天数
     */
    @Min(value = 0, message = "延迟结算天数不能小于0")
    @Max(value = 365, message = "延迟结算天数不能大于365")
    private Integer settlementDelayDays;
}
