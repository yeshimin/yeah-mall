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

    private int limitCount;

    private int limitGroup;

    private int limitGroupCount;

    // 时间窗口，单位：毫秒
    private int timeWindow;

    // 时间窗口内桶大小，单位：毫秒
    private int bucketSize;

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

    public boolean isSkip() {
        return !enabled || (limitCount < 0 && limitGroup < 0 && limitGroupCount < 0);
    }
}
