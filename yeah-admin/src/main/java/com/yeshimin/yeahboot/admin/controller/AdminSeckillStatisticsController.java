package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.vo.SeckillDashboardVo;
import com.yeshimin.yeahboot.admin.service.AdminSeckillStatisticsService;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台秒杀监控和经营分析
 */
@RestController
@RequestMapping("/admin/seckill")
@RequiredArgsConstructor
public class AdminSeckillStatisticsController {

    private final AdminSeckillStatisticsService service;

    /**
     * 查询实时监控数据
     */
    @GetMapping("/monitor/data")
    public R<SeckillDashboardVo> monitorData(@RequestParam Long activityId) {
        return R.ok(service.queryDashboard(activityId));
    }

    /**
     * 查询经营分析数据
     */
    @GetMapping("/analysis/data")
    public R<SeckillDashboardVo> analysisData(@RequestParam Long activityId) {
        return R.ok(service.queryDashboard(activityId));
    }
}
