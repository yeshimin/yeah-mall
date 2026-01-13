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
     * 状态：1-待付款 2-待发货 3-待收货 4-交易成功 5-交易关闭 6-退款 7-售后
     */
    @QueryField(type = QueryField.Type.EQ)
    private String status;

    /**
     * 退款状态：0-无 1-申请中 2-处理中 3-退款成功 4-已拒绝
     */
    @QueryField(type = QueryField.Type.EQ)
    private String refundStatus;

    /**
     * 售后状态：0-无 1-申请中 2-处理中 3-售后完成 4-已驳回
     */
    @QueryField(type = QueryField.Type.EQ)
    private String afterSaleStatus;

    /**
     * 是否已评价
     */
    @QueryField(type = QueryField.Type.EQ)
    private Boolean reviewed;

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
