package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.domain.vo.MchDashboardVo;
import com.yeshimin.yeahboot.merchant.service.MchDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/overview")
    public R<MchDashboardVo> overview(@RequestParam Long shopId) {
        return R.ok(service.queryOverview(super.getUserId(), shopId));
    }
}
