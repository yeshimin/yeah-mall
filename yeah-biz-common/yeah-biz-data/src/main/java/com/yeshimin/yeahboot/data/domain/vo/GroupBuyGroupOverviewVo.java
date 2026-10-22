package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 拼团活动下的团概览视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyGroupOverviewVo extends BaseDomain {

    /**
     * 团ID
     */
    private Long id;

    /**
     * 团编号
     */
    private String groupNo;

    /**
     * 拼团商品名称
     */
    private String productName;

    /**
     * 拼团商品主图
     */
    private String productImage;

    /**
     * 团长名称
     */
    private String leaderMemberName;

    /**
     * 团长头像
     */
    private String leaderMemberAvatar;

    /**
     * 已支付成员数
     */
    private Integer memberCount;

    /**
     * 已占席成员数，包含待支付成员
     */
    private Integer reservedMemberCount;

    /**
     * 每团成团人数
     */
    private Integer minPeople;

    /**
     * 团状态
     */
    private String status;

    /**
     * 开团时间
     */
    private LocalDateTime createTime;

    /**
     * 成团时间
     */
    private LocalDateTime successTime;

    /**
     * 失败时间
     */
    private LocalDateTime failedTime;
}
