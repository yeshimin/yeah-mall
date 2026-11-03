package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.MallAppContextVo;
import com.yeshimin.yeahboot.app.domain.vo.MallShopBannerVo;
import com.yeshimin.yeahboot.app.service.MallAppContextService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商城App上下文
 */
@RestController
@RequestMapping("/app/context")
@RequiredArgsConstructor
public class AppContextController {

    private final MallAppContextService service;

    /**
     * 查询当前商城App上下文
     */
    @PublicAccess
    @GetMapping
    public R<MallAppContextVo> context() {
        return R.ok(service.getContext());
    }

    /**
     * 查询当前独立店铺Banner
     */
    @PublicAccess
    @GetMapping("/banners")
    public R<List<MallShopBannerVo>> banners() {
        return R.ok(service.queryShopBanners());
    }
}
