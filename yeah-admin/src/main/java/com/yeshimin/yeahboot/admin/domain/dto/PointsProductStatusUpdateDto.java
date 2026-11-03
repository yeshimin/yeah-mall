package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 积分商品状态更新参数
 */
@Data
public class PointsProductStatusUpdateDto {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /**
     * 是否上架
     */
    @NotNull(message = "上架状态不能为空")
    private Boolean isEnabled;
}
