package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivityCacheVo extends BaseDomain {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityBeginTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;
}
