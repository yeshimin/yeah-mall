package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商家查询店铺订单DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchOrderQueryDto extends ShopDataBaseDomain {

    /**
     * 订单编号
     */
    @QueryField(type = QueryField.Type.LIKE)
    private String orderNo;

    /**
     * 订单状态
     */
    @QueryField(type = QueryField.Type.EQ)
    private String status;

    /**
     * 退款状态
     */
    @QueryField(type = QueryField.Type.EQ)
    private String refundStatus;

    /**
     * 售后状态
     */
    @QueryField(type = QueryField.Type.EQ)
    private String afterSaleStatus;

    /**
     * 是否已评价
     */
    @QueryField(type = QueryField.Type.EQ)
    private Boolean isReviewed;

    /**
     * 订单创建时间
     */
    @QueryField(type = QueryField.Type.BETWEEN)
    private LocalDateTime[] createTime;

    /**
     * 支付成功时间
     */
    @QueryField(type = QueryField.Type.BETWEEN)
    private LocalDateTime[] paySuccessTime;
}
