package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 订单确认收货
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfirmReceiveDto extends BaseDomain {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}
