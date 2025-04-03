package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.BaseController;
import com.yeshimin.yeahboot.upms.domain.base.R;
import com.yeshimin.yeahboot.upms.domain.dto.LoginDto;
import com.yeshimin.yeahboot.upms.domain.vo.CaptchaVo;
import com.yeshimin.yeahboot.upms.domain.vo.LoginVo;
import com.yeshimin.yeahboot.upms.service.AuthService;
import com.yeshimin.yeahboot.upms.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 登录
     */
    @PostMapping("/login")
    public R<LoginVo> login(@RequestBody LoginDto dto) {
        captchaService.checkCaptcha(dto.getKey(), dto.getCode());
        return R.ok(authService.login(dto));
    }

    /**
     * 图形验证码
     */
    @GetMapping("/captcha")
    public R<CaptchaVo> captcha() {
        return R.ok(captchaService.generateCaptcha());
    }
}
