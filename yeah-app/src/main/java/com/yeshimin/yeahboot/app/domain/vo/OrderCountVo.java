package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 个人订单数量信息VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCountVo extends BaseDomain {

    /**
     * 待付款数量
     */
    private Long waitPayCount;

    /**
     * 待发货数量
     */
    private Long waitShipCount;

    /**
     * 待收货数量
     */
    private Long waitReceiveCount;

    /**
     * 待评价数量
     */
    private Long waitCommentCount;

    /**
     * 退款/售后数量
     */
    private Long refundAndAfterSaleCount;
}
