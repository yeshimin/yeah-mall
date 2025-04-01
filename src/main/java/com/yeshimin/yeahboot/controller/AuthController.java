package com.yeshimin.yeahboot.controller;

import com.yeshimin.yeahboot.controller.base.BaseController;
import com.yeshimin.yeahboot.domain.base.R;
import com.yeshimin.yeahboot.domain.dto.LoginDto;
import com.yeshimin.yeahboot.domain.vo.LoginVo;
import com.yeshimin.yeahboot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 鉴权相关
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    /**
     * 登录
     */
    @RequestMapping("/login")
    public R<LoginVo> login(@RequestBody LoginDto dto) {
        return R.ok(authService.login(dto));
    }
}
