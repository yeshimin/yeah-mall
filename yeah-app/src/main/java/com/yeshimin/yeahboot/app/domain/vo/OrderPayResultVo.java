package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单支付结果VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单支付结果VO")
public class OrderPayResultVo extends BaseDomain {

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean paySuccess;

    /**
     * 支付成功时间
     */
    @Schema(description = "支付成功时间")
    private LocalDateTime paySuccessTime;
}
