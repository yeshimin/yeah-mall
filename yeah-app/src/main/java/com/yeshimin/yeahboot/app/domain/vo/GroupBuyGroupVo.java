package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * App拼团信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyGroupVo extends BaseDomain {

    /**
     * 团ID
     */
    private Long id;

    /**
     * 团编号
     */
    private String groupNo;

    /**
     * 已支付成员数
     */
    private Integer memberCount;

    /**
     * 已占席成员数
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
     * 团长名称
     */
    private String leaderMemberName;

    /**
     * 团长头像
     */
    private String leaderMemberAvatar;
}
