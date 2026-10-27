package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 积分订单人工履约参数
 */
@Data
public class PointsOrderFulfillDto {

    /**
     * 积分订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long id;

    /**
     * 履约说明
     */
    @NotBlank(message = "履约说明不能为空")
    private String remark;
}
