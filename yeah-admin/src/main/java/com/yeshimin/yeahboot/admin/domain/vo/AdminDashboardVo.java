package com.yeshimin.yeahboot.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台经营概览
 */
@Data
public class AdminDashboardVo {

    /**
     * 今日支付订单数
     */
    private Long todayOrderCount;

    /**
     * 今日交易额
     */
    private BigDecimal todaySalesAmount;

    /**
     * 昨日支付订单数
     */
    private Long yesterdayOrderCount;

    /**
     * 昨日交易额
     */
    private BigDecimal yesterdaySalesAmount;

    /**
     * 启用中的店铺数
     */
    private Long activeShopCount;

    /**
     * 店铺总数
     */
    private Long totalShopCount;

    /**
     * 退款待处理订单数
     */
    private Long refundPendingCount;

    /**
     * 退款待处理涉及店铺数
     */
    private Long refundShopCount;

    /**
     * 待启用商家数
     */
    private Long pendingMerchantCount;

    /**
     * 秒杀报名待审数
     */
    private Long seckillApplyPendingCount;

    /**
     * 已隐藏评价数
     */
    private Long hiddenReviewCount;

    /**
     * 意见反馈待处理数
     */
    private Long feedbackPendingCount;
}
