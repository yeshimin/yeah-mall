package com.yeshimin.yeahboot.flowcontrol.ratelimit;

import com.yeshimin.yeahboot.flowcontrol.enums.GroupType;

import java.lang.annotation.*;

/**
 * 限流注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 是否启用
     */
    boolean enabled() default true;

    /**
     * 名称，默认为方法的全限定名
     */
    String name() default "";

    /**
     * 分组方式：1-无 2-按IP 3-按用户
     */
    GroupType groupType() default GroupType.NONE;

    /**
     * 自定义分组逻辑，配合GroupType.CUSTOM使用；支持spel表达式
     */
    String customGroup() default "";

    /**
     * 限制数量
     */
    int limitCount() default -1;

    /**
     * 限制分组数量
     */
    int limitGroup() default -1;

    /**
     * 限制每个分组内数量
     */
    int limitGroupCount() default -1;

    /**
     * 时间窗口，单位：毫秒
     */
    int timeWindow() default 1000;

    /**
     * 时间窗口内桶大小，单位：毫秒，默认：100
     */
    int bucketSize() default 100;

    /**
     * 是否动态时间窗口模式
     */
    boolean dynamicTimeWindow() default true;

    /**
     * 是否全局限流 TODO
     */
    boolean global() default false;

    /**
     * 描述
     */
    String description() default "";
}
