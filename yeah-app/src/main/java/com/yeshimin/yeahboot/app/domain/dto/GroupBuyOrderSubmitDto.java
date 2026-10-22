package com.yeshimin.yeahboot.app.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 拼团订单提交参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyOrderSubmitDto extends OrderSubmitDto {

    /**
     * 拼团商品SKU ID
     */
    @NotNull(message = "拼团商品SKU ID不能为空")
    private Long groupBuySkuId;

    /**
     * 要参与的团ID；为空时创建新团
     */
    private Long groupId;

    /**
     * 是否随机加入一个可参与的团
     */
    private Boolean randomJoin;
}
