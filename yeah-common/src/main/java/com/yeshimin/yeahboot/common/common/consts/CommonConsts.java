package com.yeshimin.yeahboot.common.common.consts;

import java.time.LocalDateTime;

/**
 * 公共常量
 * 未确定归属的可以先放这里
 */
public class CommonConsts {

    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";

    public static final String TOKEN_HEADER_KEY = "Authorization";

    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
    public static final String DEFAULT_TIME_ZONE = "GMT+8";

    /**
     * 树形结构的根节点ID
     */
    public static final long TREE_ROOT_ID = 0L;

    public static final String PROJECT_NAME = "yeah-boot";

    // max time
    public static final LocalDateTime MAX_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    // 自定义查询字段名，添加_后缀（前缀方式jackson解析不了）
    public static final String CONDITIONS_FIELD_NAME = "conditions_";

    // 验证码缓存key
    public static final String CAPTCHA_KEY = "captcha_key:%s";

    // 用户token过期时间（3600s）
    public static final long USER_TOKEN_EXPIRE_SECONDS = 3600;

    // jwt token 'terminal' claim name
    public static final String JWT_CLAIM_TERMINAL = "term";
}