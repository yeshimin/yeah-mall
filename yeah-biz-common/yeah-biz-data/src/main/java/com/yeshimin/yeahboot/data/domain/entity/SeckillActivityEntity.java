package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 秒杀活动表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_seckill_activity")
public class SeckillActivityEntity extends ConditionBaseEntity<SeckillActivityEntity> {

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动封面
     */
    private String coverImage;

    /**
     * 报名开始时间
     */
    private LocalDateTime applyBeginTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime applyEndTime;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityBeginTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;

    /**
     * 活动状态：见代码枚举 SeckillActivityStatusEnum
     */
    private String status;

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
