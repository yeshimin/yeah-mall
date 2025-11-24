package com.yeshimin.yeahboot.app.demo;

import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.ratelimit.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 限流测试
 */
@RestController
@RequestMapping("/app/demo/ratelimit")
public class DemoRateLimitController {

    @PublicAccess
    @RateLimit(timeWindow = 2000, limitCount = 5, description = "限制2秒内最多5个请求")
    @GetMapping("/test")
    public String test() {
        return "hello world";
    }
}
