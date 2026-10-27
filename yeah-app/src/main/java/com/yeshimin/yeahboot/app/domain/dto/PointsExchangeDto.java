package com.yeshimin.yeahboot.app.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 积分商品兑换参数
 */
@Data
public class PointsExchangeDto {

    /**
     * 积分商品SKU ID
     */
    @NotNull(message = "请选择兑换规格")
    private Long skuId;

    /**
     * 兑换数量
     */
    @NotNull(message = "兑换数量不能为空")
    @Min(value = 1, message = "兑换数量不能小于1")
    private Integer quantity;

    /**
     * 收货地址ID，物流商品必填
     */
    private Long addressId;

    /**
     * 客户端幂等请求号
     */
    @NotBlank(message = "请求号不能为空")
    @Size(max = 64, message = "请求号长度不能超过64个字符")
    private String requestNo;
}
