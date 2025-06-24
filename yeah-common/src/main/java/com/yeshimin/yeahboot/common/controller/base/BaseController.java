package com.yeshimin.yeahboot.common.controller.base;

import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;

/**
 * 基类Controller
 */
public abstract class BaseController {

    /**
     * 获取当前会话用户ID
     */
    protected Long getUserId() {
        return WebContextUtils.getUserId();
    }
}
