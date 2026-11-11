package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 经营概览按日交易趋势
 */
@Data
public class DashboardTrendVo {

    /**
     * 日期，格式 yyyy-MM-dd
     */
    private String date;

    /**
     * 支付订单数
     */
    private Long orderCount;

    /**
     * 支付金额
     */
    private BigDecimal salesAmount;
}
