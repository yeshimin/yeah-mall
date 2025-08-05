package com.yeshimin.yeahboot.auth.common.config.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PublicAccess {

    boolean enabled() default true;
}
