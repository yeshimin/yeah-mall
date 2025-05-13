package com.yeshimin.yeahboot.upms.controller;

import cn.hutool.core.util.BooleanUtil;
import com.yeshimin.yeahboot.common.common.log.SysLog;
import com.yeshimin.yeahboot.common.common.properties.YeahBootProperties;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.upms.domain.dto.LoginDto;
import com.yeshimin.yeahboot.upms.domain.vo.CaptchaVo;
import com.yeshimin.yeahboot.upms.domain.vo.LoginVo;
import com.yeshimin.yeahboot.upms.service.AuthService;
import com.yeshimin.yeahboot.upms.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 鉴权相关
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;
    private final CaptchaService captchaService;

    private final YeahBootProperties yeahBootProperties;

    /**
     * 登录
     */
    @SysLog("登录")
    @PostMapping("/login")
    public R<LoginVo> login(@Valid @RequestBody LoginDto dto) {
        if (BooleanUtil.isTrue(yeahBootProperties.getCaptchaEnabled())) {
            captchaService.checkCaptcha(dto.getKey(), dto.getCode());
        }
        return R.ok(authService.login(dto));
    }

    /**
     * 图形验证码
     */
    @GetMapping("/captcha")
    public R<CaptchaVo> captcha() {
        Boolean captchaEnabled = yeahBootProperties.getCaptchaEnabled();
        CaptchaVo vo = captchaEnabled ? captchaService.generateCaptcha() : new CaptchaVo();
        vo.setEnabled(captchaEnabled);
        return R.ok(vo);
    }
}
