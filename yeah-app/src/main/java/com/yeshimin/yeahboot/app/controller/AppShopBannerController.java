package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.MallShopBannerVo;
import com.yeshimin.yeahboot.app.service.AppShopBannerService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 店铺Banner
 */
@RestController
@RequestMapping("/app/shop/banner")
@RequiredArgsConstructor
public class AppShopBannerController {

    private final AppShopBannerService service;

    /**
     * 查询店铺Banner
     */
    @PublicAccess
    @GetMapping("/query")
    public R<List<MallShopBannerVo>> query(@RequestParam Long shopId) {
        return R.ok(service.query(shopId));
    }
}
