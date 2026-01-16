package com.yeshimin.yeahboot.app.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderSubmitVo extends WxPayInfoVo {

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;
}
