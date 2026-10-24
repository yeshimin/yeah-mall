package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家经营首页概览
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchDashboardVo extends BaseDomain {

    /**
     * 待发货订单数
     */
    private Long waitShipCount;

    /**
     * 待处理退款数
     */
    private Long refundPendingCount;

    /**
     * 库存预警SKU数
     */
    private Long stockWarningCount;

    /**
     * 今日订单数
     */
    private Long todayOrderCount;

    /**
     * 今日销售额
     */
    private BigDecimal todaySalesAmount;

    /**
     * 今日统计开始时间
     */
    private LocalDateTime todayBeginTime;

    /**
     * 今日统计结束时间
     */
    private LocalDateTime todayEndTime;

    /**
     * 库存预警阈值
     */
    private Integer stockWarningThreshold;

    /**
     * 低库存SKU列表
     */
    private List<MchStockWarningVo> stockWarnings;
}
