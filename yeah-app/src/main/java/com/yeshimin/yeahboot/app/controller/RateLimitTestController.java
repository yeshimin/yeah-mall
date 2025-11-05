package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.ratelimit.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 限流测试
 */
@RestController
@RequestMapping("/app/ratelimit/test")
public class RateLimitTestController {

    @PublicAccess
    @RateLimit(limitCount = 5, timeWindow = 1)
    @GetMapping("/test")
    public String test() {
        return "hello world";
    }
}
