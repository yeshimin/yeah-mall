package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.service.WxPayInfoVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀业务处理结果VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillBizResultVo extends WxPayInfoVo {

    /**
     * 秒杀处理是否成功
     */
    @Schema(description = "秒杀处理是否成功")
    private Boolean success;

    /**
     * 消息
     */
    @Schema(description = "消息")
    private String message;

    /**
     * 订单和支付信息等数据
     */
    @Schema(description = "订单和支付信息等数据")
    private OrderSubmitVo data;
}
