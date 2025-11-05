package com.yeshimin.yeahboot.ratelimit;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimits {

    boolean enabled() default true;

    RateLimit[] value();
}
