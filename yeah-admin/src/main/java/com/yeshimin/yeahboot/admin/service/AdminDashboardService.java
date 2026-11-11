package com.yeshimin.yeahboot.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yeshimin.yeahboot.admin.domain.vo.AdminDashboardVo;
import com.yeshimin.yeahboot.data.common.enums.FeedbackStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.RefundStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.DashboardTrendVo;
import com.yeshimin.yeahboot.data.repository.MemberFeedbackRepo;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewRepo;
import com.yeshimin.yeahboot.data.repository.SeckillActivityApplyRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.service.DashboardTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 平台经营概览服务
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderRepo orderRepo;
    private final ShopRepo shopRepo;
    private final MerchantRepo merchantRepo;
    private final SeckillActivityApplyRepo seckillActivityApplyRepo;
    private final ProductSkuReviewRepo productSkuReviewRepo;
    private final MemberFeedbackRepo memberFeedbackRepo;
    private final DashboardTrendService dashboardTrendService;

    /**
     * 查询平台经营概览。
     */
    public AdminDashboardVo queryOverview() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayBegin = today.atStartOfDay();
        LocalDateTime tomorrowBegin = todayBegin.plusDays(1);
        LocalDateTime yesterdayBegin = todayBegin.minusDays(1);

        AdminDashboardVo result = new AdminDashboardVo();
        result.setTodayOrderCount(countPaidOrders(todayBegin, tomorrowBegin));
        result.setTodaySalesAmount(sumPaidOrders(todayBegin, tomorrowBegin));
        result.setYesterdayOrderCount(countPaidOrders(yesterdayBegin, todayBegin));
        result.setYesterdaySalesAmount(sumPaidOrders(yesterdayBegin, todayBegin));
        result.setActiveShopCount(shopRepo.lambdaQuery()
                .eq(ShopEntity::getIsEnabled, true)
                .count());
        result.setTotalShopCount(shopRepo.count());
        result.setRefundPendingCount(orderRepo.lambdaQuery()
                .eq(OrderEntity::getStatus, OrderStatusEnum.REFUND.getValue())
                .in(OrderEntity::getRefundStatus, RefundStatusEnum.APPLYING.getValue(),
                        RefundStatusEnum.PROCESSING.getValue())
                .count());
        result.setRefundShopCount(countRefundShops());
        result.setPendingMerchantCount(merchantRepo.lambdaQuery()
                .eq(MerchantEntity::getIsEnabled, false)
                .count());
        result.setSeckillApplyPendingCount(seckillActivityApplyRepo.lambdaQuery()
                .eq(SeckillActivityApplyEntity::getAuditStatus,
                        Integer.valueOf(SeckillActivityApplyAuditStatusEnum.PENDING.getValue()))
                .count());
        result.setHiddenReviewCount(productSkuReviewRepo.lambdaQuery()
                .eq(ProductSkuReviewEntity::getIsVisible, false)
                .count());
        result.setFeedbackPendingCount(memberFeedbackRepo.lambdaQuery()
                .eq(MemberFeedbackEntity::getStatus, FeedbackStatusEnum.PENDING.getValue())
                .count());
        return result;
    }

    /**
     * 查询平台近 N 日交易趋势。
     */
    public List<DashboardTrendVo> queryTrend(Integer days) {
        return dashboardTrendService.queryTrend(null, days);
    }

    private long countPaidOrders(LocalDateTime begin, LocalDateTime end) {
        return orderRepo.count(paidOrderWrapper(begin, end));
    }

    private BigDecimal sumPaidOrders(LocalDateTime begin, LocalDateTime end) {
        QueryWrapper<OrderEntity> wrapper = paidOrderWrapper(begin, end);
        wrapper.select("IFNULL(SUM(final_amount), 0) AS sales_amount");
        List<Map<String, Object>> rows = orderRepo.getBaseMapper().selectMaps(wrapper);
        if (rows.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Object value = rows.get(0).get("sales_amount");
        return value == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(value));
    }

    private long countRefundShops() {
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(DISTINCT shop_id) AS shop_count")
                .eq("status", OrderStatusEnum.REFUND.getValue())
                .in("refund_status", RefundStatusEnum.APPLYING.getValue(),
                        RefundStatusEnum.PROCESSING.getValue());
        List<Map<String, Object>> rows = orderRepo.getBaseMapper().selectMaps(wrapper);
        if (rows.isEmpty()) {
            return 0L;
        }
        Object value = rows.get(0).get("shop_count");
        return value == null ? 0L : Long.parseLong(String.valueOf(value));
    }

    private QueryWrapper<OrderEntity> paidOrderWrapper(LocalDateTime begin, LocalDateTime end) {
        return new QueryWrapper<OrderEntity>()
                .ge("pay_success_time", begin)
                .lt("pay_success_time", end)
                .notIn("status", OrderStatusEnum.CLOSED.getValue(),
                        OrderStatusEnum.REFUND.getValue(), OrderStatusEnum.AFTER_SALE.getValue());
    }
}
