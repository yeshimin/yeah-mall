package com.yeshimin.yeahboot.merchant.auth;

import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.common.log.SysLog;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.flowcontrol.enums.GroupType;
import com.yeshimin.yeahboot.flowcontrol.ratelimit.RateLimit;
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
@RequestMapping("/mch/auth")
@RequiredArgsConstructor
public class MerchantAuthController extends BaseController {

    private final MerchantAuthService merchantAuthService;

    /**
     * 登录
     */
    @PublicAccess
    @RateLimit(groupType = GroupType.IP, limitGroupCount = 20, timeWindow = 60_000,
            description = "同一IP一分钟最多登录20次")
    @SysLog("登录")
    @PostMapping("/login")
    public R<LoginVo> login(@Valid @RequestBody LoginDto dto) {
        return R.ok(merchantAuthService.login(dto));
    }
}
