package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拼团订单详情补充信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderGroupBuyInfoVo extends BaseDomain {

    /**
     * 团编号
     */
    private String groupNo;

    /**
     * 团状态
     */
    private String groupStatus;

    /**
     * 已支付成员数
     */
    private Integer memberCount;

    /**
     * 每团成团人数
     */
    private Integer minPeople;

    /**
     * 当前订单是否团长订单
     */
    private Boolean leader;
}
