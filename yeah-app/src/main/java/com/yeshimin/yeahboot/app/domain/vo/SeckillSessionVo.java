package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * App端秒杀场次VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillSessionVo extends BaseDomain {

    /**
     * 场次ID
     */
    private Long id;

    /**
     * 场次名称
     */
    private String name;

    /**
     * 场次开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 场次结束时间
     */
    private LocalDateTime endTime;

    /**
     * 当前是否允许抢购
     */
    private Boolean purchasable;
}
