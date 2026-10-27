package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 积分订单发货参数
 */
@Data
public class PointsOrderShipDto {

    /**
     * 积分订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long id;

    /**
     * 物流公司编码
     */
    @NotBlank(message = "物流公司不能为空")
    private String providerCode;

    /**
     * 物流单号
     */
    @NotBlank(message = "物流单号不能为空")
    private String trackingNo;
}
