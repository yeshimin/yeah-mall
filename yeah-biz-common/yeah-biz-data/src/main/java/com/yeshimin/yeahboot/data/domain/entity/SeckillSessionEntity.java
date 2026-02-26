package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 秒杀活动场次表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_seckill_session")
public class SeckillSessionEntity extends ConditionBaseEntity<SeckillSessionEntity> {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动场次名称
     */
    private String name;

    /**
     * 活动开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 活动截止时间
     */
    private LocalDateTime endTime;

    /**
     * 排序：大于等于1
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 备注
     */
    private String remark;
}
