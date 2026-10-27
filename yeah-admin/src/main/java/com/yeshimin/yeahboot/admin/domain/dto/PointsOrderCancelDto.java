package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 积分订单取消参数
 */
@Data
public class PointsOrderCancelDto {

    /**
     * 积分订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long id;

    /**
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    private String reason;
}
