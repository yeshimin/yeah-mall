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
 * 店铺更新参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopUpdateDto extends BaseDomain {

    /**
     * 主键ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long id;

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
