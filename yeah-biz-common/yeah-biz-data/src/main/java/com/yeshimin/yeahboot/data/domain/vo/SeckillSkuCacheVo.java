package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 秒杀SKU缓存VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillSkuCacheVo extends BaseDomain {

    /**
     * 秒杀SKU ID
     */
    private Long skuId;

    /**
     * 秒杀商品SPU ID
     */
    private Long seckillSpuId;

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 场次开始时间
     */
    private LocalDateTime sessionBeginTime;

    /**
     * 场次结束时间
     */
    private LocalDateTime sessionEndTime;
}
