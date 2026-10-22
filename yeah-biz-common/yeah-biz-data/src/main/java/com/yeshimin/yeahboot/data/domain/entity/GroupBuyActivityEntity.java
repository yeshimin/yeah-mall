package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 拼团活动表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_group_buy_activity")
public class GroupBuyActivityEntity extends ConditionBaseEntity<GroupBuyActivityEntity> {

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
     * 每团成团人数
     */
    private Integer minPeople;

    /**
     * 最大开团数，-1表示不限，0表示未设置
     */
    private Integer groupLimit;

    /**
     * 活动状态
     */
    private String status;

    /**
     * 排序
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
