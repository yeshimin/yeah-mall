package com.yeshimin.yeahboot.auth.common.config.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PublicAccess {

    boolean enabled() default true;

    /**
     * 按需鉴权：当为true，表示如果token存在则进行鉴权
     * 默认false，表示当enabled=true时，明确不进行鉴权
     * 前置条件：当enabled=true
     */
    boolean authOnDemand() default false;
}
