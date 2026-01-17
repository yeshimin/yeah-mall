package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateShipInfoDto extends BaseDomain {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 物流公司编码
     */
    @NotBlank(message = "物流公司编码不能为空")
    private String deliveryProviderCode;

    /**
     * 快递单号
     */
    @NotBlank(message = "快递单号不能为空")
    private String trackingNo;
}
