package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 确认退款DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfirmRefundDto extends BaseDomain {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 退款备注
     */
    private String remark;
}
