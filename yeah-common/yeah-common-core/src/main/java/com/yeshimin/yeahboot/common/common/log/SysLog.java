package com.yeshimin.yeahboot.common.common.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SysLog {

    String value() default "";

    // 触发类型：1-系统自动 2-用户手动
    String triggerType() default "1";

    // 事件类型：1-鉴权相关（登录、登出、续期等） 2-数据操作 3-定时任务
    String category() default "2";
}
