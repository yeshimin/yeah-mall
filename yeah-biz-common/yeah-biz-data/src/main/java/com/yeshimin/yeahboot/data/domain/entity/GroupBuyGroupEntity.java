package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 拼团团表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_group_buy_group")
public class GroupBuyGroupEntity extends ShopConditionBaseEntity<GroupBuyGroupEntity> {

    /**
     * 团编号
     */
    private String groupNo;

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 拼团商品SPU ID
     */
    private Long groupBuySpuId;

    /**
     * 团长会员ID
     */
    private Long leaderMemberId;

    /**
     * 已支付成员数
     */
    private Integer memberCount;

    /**
     * 已占席成员数，包含待支付与已支付成员
     */
    private Integer reservedMemberCount;

    /**
     * 每团成团人数快照
     */
    private Integer minPeople;

    /**
     * 成团时间
     */
    private LocalDateTime successTime;

    /**
     * 失败时间
     */
    private LocalDateTime failedTime;

    /**
     * 团状态
     */
    private String status;
}
