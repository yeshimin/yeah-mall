package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.AppPlatQuickEntryVo;
import com.yeshimin.yeahboot.app.service.AppPlatQuickEntryService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * APP端平台快捷入口表
 */
@RestController
@RequestMapping("/app/platQuickEntry")
@RequiredArgsConstructor
public class AppPlatQuickEntryController extends BaseController {

    private final AppPlatQuickEntryService service;

    /**
     * 查询
     */
    @PublicAccess
    @GetMapping("/query")
    public R<List<AppPlatQuickEntryVo>> query() {
        return R.ok(service.query());
    }
}
