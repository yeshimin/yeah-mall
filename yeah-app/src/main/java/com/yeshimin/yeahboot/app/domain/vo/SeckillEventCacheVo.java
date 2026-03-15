package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 秒杀sku用户的各个事件节点时间缓存值VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillEventCacheVo extends BaseDomain {

    private LocalDateTime quotaTime;

    private LocalDateTime orderTime;

    private Long orderId;

    private LocalDateTime payTime;
}
