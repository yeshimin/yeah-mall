package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拼团成员视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyGroupMemberVo extends BaseDomain {

    /**
     * 拼团订单关联ID
     */
    private Long id;

    /**
     * 主订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员头像
     */
    private String memberAvatar;

    /**
     * 是否团长
     */
    private Boolean isLeader;

    /**
     * 拼团参与状态
     */
    private String orderStatus;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

    /**
     * 支付成功时间
     */
    private LocalDateTime payTime;
}