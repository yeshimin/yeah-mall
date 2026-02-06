package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.PlatBannerVo;
import com.yeshimin.yeahboot.app.service.AppPlatBannerService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 平台Banner相关
 */
@RestController
@RequestMapping("/app/platBanner")
@RequiredArgsConstructor
public class AppPlatBannerController extends BaseController {

    private final AppPlatBannerService service;

    /**
     * 查询省份
     */
    @PublicAccess
    @GetMapping("/query")
    public R<List<PlatBannerVo>> query() {
        return R.ok(service.query());
    }
}
