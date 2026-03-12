package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.service.WxPayInfoVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderSeckillVo extends WxPayInfoVo {

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 消息
     */
    @Schema(description = "消息")
    private String message;

    /**
     * skuId
     */
    @Schema(description = "skuId")
    private Long skuId;
}
