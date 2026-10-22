package com.yeshimin.yeahboot.admin.service;

import com.yeshimin.yeahboot.admin.domain.vo.SeckillDashboardVo;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillStatisticsVo;
import com.yeshimin.yeahboot.data.mapper.SeckillSpuMapper;
import com.yeshimin.yeahboot.data.repository.SeckillActivityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 平台秒杀订单和库存统计服务
 */
@Service
@RequiredArgsConstructor
public class AdminSeckillStatisticsService {

    private final SeckillActivityRepo seckillActivityRepo;
    private final SeckillSpuMapper seckillSpuMapper;

    /**
     * 查询秒杀活动看板数据
     */
    public SeckillDashboardVo queryDashboard(Long activityId) {
        SeckillActivityEntity activity = seckillActivityRepo.getOneById(activityId, "秒杀活动不存在");
        // 汇总订单、参与用户、成交额和可售库存，排行榜与总览使用同一活动范围。
        SeckillStatisticsVo statistics = seckillSpuMapper.queryActivityStatistics(activity.getId());

        SeckillDashboardVo result = new SeckillDashboardVo();
        result.setPaidOrderCount(statistics.getPaidOrderCount());
        result.setParticipantCount(statistics.getParticipantCount());
        result.setSalesAmount(statistics.getSalesAmount());
        result.setRemainingStock(statistics.getRemainingStock());
        // 排行榜由Mapper按有效成交额和销量排序，避免前端再次聚合。
        result.setProductRanking(seckillSpuMapper.queryActivityProductStatistics(activity.getId()));
        return result;
    }
}
