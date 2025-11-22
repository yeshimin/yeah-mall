package com.yeshimin.yeahboot.ratelimit;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public interface RateLimitService {

    String getGroupName(HttpServletRequest request, Method method);

    String getUserId(HttpServletRequest request, Method method);

    String getIp(HttpServletRequest request, Method method);
}
