package com.yeshimin.yeahboot.ratelimit;

import com.yeshimin.yeahboot.ratelimit.enums.GroupType;
import lombok.Data;

/**
 * RateLimit 配置类
 */
@Data
class RateLimitConf {

    private boolean enabled;

    private String name;

    private GroupType groupType;

    private String customGroup;

    private long limitCount;

    private long limitGroup;

    private long limitGroupCount;

    private long timeWindow;

    private boolean dynamicTimeWindow;

    private boolean global;

    // --------------------------------------------------------------------------------

    // 计算得出
    private long windowFrom;

    // 计算得出
    private long windowTo;

    // 请求时间
    private long reqTime;

    // 计算得出
    private String resName;

    // 计算得出
    private String groupName;
}
