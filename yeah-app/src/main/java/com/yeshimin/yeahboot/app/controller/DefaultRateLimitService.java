package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.ratelimit.RateLimitService;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class DefaultRateLimitService implements RateLimitService {

    @Override
    public String getGroupName(HttpServletRequest request, Method method) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public String getUserId(HttpServletRequest request, Method method) {
        return String.valueOf(WebContextUtils.getUserId());
    }

    @Override
    public String getIp(HttpServletRequest request, Method method) {
        return request.getRemoteAddr();
    }
}
