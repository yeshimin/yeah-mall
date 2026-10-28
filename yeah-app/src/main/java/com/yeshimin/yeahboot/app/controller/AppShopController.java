package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.ShopDetailVo;
import com.yeshimin.yeahboot.app.service.AppShopService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺
 */
@RestController
@RequestMapping("/app/shop")
@RequiredArgsConstructor
public class AppShopController extends BaseController {

    private final AppShopService service;

    /**
     * 查询店铺详情
     */
    @PublicAccess
    @GetMapping("/detail")
    public R<ShopDetailVo> detail(@RequestParam Long shopId) {
        return R.ok(service.detail(shopId));
    }
}
