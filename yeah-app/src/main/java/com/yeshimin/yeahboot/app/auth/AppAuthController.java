package com.yeshimin.yeahboot.app.auth;

import com.yeshimin.yeahboot.common.common.log.SysLog;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 鉴权相关
 */
@Slf4j
@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AppAuthController extends BaseController {

    private final AppAuthService appAuthService;

    /**
     * 登录
     */
    @SysLog("登录")
    @PostMapping("/login")
    public R<LoginVo> login(@Valid @RequestBody LoginDto dto) {
        return R.ok(appAuthService.login(dto));
    }
}
