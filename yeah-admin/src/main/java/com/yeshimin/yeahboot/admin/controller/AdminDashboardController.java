package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.vo.AdminDashboardVo;
import com.yeshimin.yeahboot.admin.service.AdminDashboardService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.vo.DashboardTrendVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 平台经营概览接口
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController extends BaseController {

    private final AdminDashboardService service;

    /**
     * 查询平台经营概览。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/overview")
    public R<AdminDashboardVo> overview() {
        return R.ok(service.queryOverview());
    }

    /**
     * 查询平台近 N 日交易趋势。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/trend")
    public R<List<DashboardTrendVo>> trend(@RequestParam(defaultValue = "7") Integer days) {
        return R.ok(service.queryTrend(days));
    }
}
