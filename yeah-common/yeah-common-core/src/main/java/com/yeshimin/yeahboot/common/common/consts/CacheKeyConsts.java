package com.yeshimin.yeahboot.common.common.consts;

/**
 * 缓存键常量类
 */
public class CacheKeyConsts {

    /**
     * 用户token缓存key
     * 带终端的 subject : user : terminal
     */
    public static final String USER_TOKEN_TERM = "sub:%s:user:%s:token:term:%s";
    // 无终端的 subject : user
    public static final String USER_TOKEN = "sub:%s:user:%s:token";
}