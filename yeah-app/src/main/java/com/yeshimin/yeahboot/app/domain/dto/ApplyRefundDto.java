package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 申请退款DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplyRefundDto extends BaseDomain {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 退款原因
     */
    @NotBlank(message = "退款原因不能为空")
    private String reason;
}
