package com.yeshimin.yeahboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.vo.DashboardTrendVo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 经营概览交易趋势，多端共用
 */
@Service
@RequiredArgsConstructor
public class DashboardTrendService {

    private static final int DEFAULT_DAYS = 7;
    private static final int MAX_DAYS = 31;

    private final OrderRepo orderRepo;

    /**
     * 查询按日交易趋势，shopId 为空时统计全平台。
     */
    public List<DashboardTrendVo> queryTrend(Long shopId, Integer days) {
        int span = normalizeDays(days);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(span - 1L);

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("DATE(pay_success_time) AS stat_date",
                        "COUNT(*) AS order_count",
                        "IFNULL(SUM(final_amount), 0) AS sales_amount")
                .ge("pay_success_time", startDate.atStartOfDay())
                .lt("pay_success_time", endDate.plusDays(1).atStartOfDay())
                .isNotNull("pay_success_time")
                .notIn("status", OrderStatusEnum.CLOSED.getValue(),
                        OrderStatusEnum.REFUND.getValue(), OrderStatusEnum.AFTER_SALE.getValue());
        if (shopId != null) {
            wrapper.eq("shop_id", shopId);
        }
        wrapper.groupBy("DATE(pay_success_time)").orderByAsc("stat_date");

        Map<String, DashboardTrendVo> values = new HashMap<>();
        for (Map<String, Object> row : orderRepo.getBaseMapper().selectMaps(wrapper)) {
            String date = String.valueOf(row.get("stat_date"));
            DashboardTrendVo vo = new DashboardTrendVo();
            vo.setDate(date);
            vo.setOrderCount(toLong(row.get("order_count")));
            vo.setSalesAmount(toAmount(row.get("sales_amount")));
            values.put(date, vo);
        }

        List<DashboardTrendVo> result = new ArrayList<>(span);
        for (int i = 0; i < span; i++) {
            String date = startDate.plusDays(i).toString();
            DashboardTrendVo vo = values.get(date);
            if (vo == null) {
                vo = new DashboardTrendVo();
                vo.setDate(date);
                vo.setOrderCount(0L);
                vo.setSalesAmount(BigDecimal.ZERO);
            }
            result.add(vo);
        }
        return result;
    }

    private int normalizeDays(Integer days) {
        if (days == null) {
            return DEFAULT_DAYS;
        }
        return Math.min(Math.max(days, 1), MAX_DAYS);
    }

    private long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return value == null ? 0L : Long.parseLong(String.valueOf(value));
    }

    private BigDecimal toAmount(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(value));
    }
}
