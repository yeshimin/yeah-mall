package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.vo.DashboardTrendVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchDashboardVo;
import com.yeshimin.yeahboot.merchant.service.MchDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商家经营首页接口
 */
@RestController
@RequestMapping("/mch/dashboard")
@RequiredArgsConstructor
public class MchDashboardController extends BaseController {

    private final MchDashboardService service;

    /**
     * 查询当前店铺经营概览。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/overview")
    public R<MchDashboardVo> overview(@RequestParam Long shopId) {
        return R.ok(service.queryOverview(super.getUserId(), shopId));
    }

    /**
     * 查询当前店铺近 N 日交易趋势。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/trend")
    public R<List<DashboardTrendVo>> trend(@RequestParam Long shopId,
                                           @RequestParam(defaultValue = "7") Integer days) {
        return R.ok(service.queryTrend(super.getUserId(), shopId, days));
    }
}
