package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.data.common.enums.AfterSaleStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
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
    @EnumValue(enumClass = OrderStatusEnum.class, groups = Query.class, message = "订单状态不正确")
    private String status;

    /**
     * 退款状态
     */
    @QueryField(type = QueryField.Type.EQ)
    @EnumValue(enumClass = RefundStatusEnum.class, groups = Query.class, message = "退款状态不正确")
    private String refundStatus;

    /**
     * 是否仅查询退款申请中或退款处理中的订单
     */
    private Boolean refundPending;

    /**
     * 售后状态
     */
    @QueryField(type = QueryField.Type.EQ)
    @EnumValue(enumClass = AfterSaleStatusEnum.class, groups = Query.class, message = "售后状态不正确")
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

    /**
     * 订单类型：见【订单类型枚举】
     */
    @QueryField(type = QueryField.Type.EQ)
    @EnumValue(enumClass = OrderTypeEnum.class, groups = Query.class, message = "订单类型不正确")
    private Integer orderType;
}
