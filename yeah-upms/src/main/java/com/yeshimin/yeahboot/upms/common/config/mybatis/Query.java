package com.yeshimin.yeahboot.upms.common.config.mybatis;

import java.lang.annotation.*;

/**
 * 查询类上可以不添加该注解，行为保持默认
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    // 是否启用，默认是
    boolean enabled() default true;

    // 是否支持客户端自定义条件查询，默认否
    boolean custom() default false;
}
