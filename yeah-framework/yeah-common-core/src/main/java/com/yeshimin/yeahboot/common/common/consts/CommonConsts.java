package com.yeshimin.yeahboot.common.common.consts;

import java.time.LocalDateTime;

/**
 * 公共常量
 * 未确定归属的可以先放这里
 */
public class CommonConsts {

    public static final String TOKEN_HEADER_KEY = "Authorization";

    // 手机号正则
    public static final String PATTERN_MOBILE = "(?:0|86|\\+86)?1[3-9]\\d{9}"; // see hutool PatternPool.MOBILE

    /**
     * 树形结构的根节点ID
     */
    public static final long ROOT_ID = 0L;

    // 默认排序步长
    public static final int DEFAULT_SORT_STEP = 10;

    // max time
    public static final LocalDateTime MAX_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    // 自定义查询字段名，添加_后缀（前缀方式jackson解析不了）
    public static final String CONDITIONS_FIELD_NAME = "conditions_";

    // 图形验证码缓存key
    public static final String CAPTCHA_KEY = "captcha_key:%s";
    // APP端短信验证码缓存key %s=手机号
    public static final String APP_SMS_CODE_KEY = "app_sms_code_key:%s";

    // jwt token 'terminal' claim name
    public static final String JWT_CLAIM_TERMINAL = "term";
    // jwt iat ms
    public static final String JWT_CLAIM_IAT_MS = "iatMs";
    // jwt exp ms
    public static final String JWT_CLAIM_EXP_MS = "expMs";
}
