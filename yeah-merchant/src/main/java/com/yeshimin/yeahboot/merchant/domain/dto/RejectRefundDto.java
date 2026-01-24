package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 拒绝退款DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RejectRefundDto extends BaseDomain {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 原因
     */
    private String reason;
}
