package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.service.AppUserService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.AppUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * app端用户相关
 */
@Slf4j
@RestController
@RequestMapping("/app/appUser")
@RequiredArgsConstructor
public class AppUserController extends BaseController {

    private final AppUserService appUserService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<AppUserEntity> detail() {
        Long userId = WebContextUtils.getUserId();
        return R.ok(appUserService.detail(userId));
    }
}
