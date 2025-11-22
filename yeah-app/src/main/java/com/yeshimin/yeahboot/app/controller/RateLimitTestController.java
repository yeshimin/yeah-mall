package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.ratelimit.RateLimit;
import com.yeshimin.yeahboot.ratelimit.enums.GroupType;
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
    @RateLimit(timeWindow = 5, limitCount = 10, limitGroupCount = 10, groupType = GroupType.CUSTOM, customGroup = "#request.getParameter('groupName')", limitGroup = 2)
    @GetMapping("/test")
    public String test() {
        return "hello world";
    }
}
