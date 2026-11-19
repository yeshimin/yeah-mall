package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拼团订单视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyOrderVo extends BaseDomain {

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
     * 团ID
     */
    private Long groupId;

    /**
     * 团编号
     */
    private String groupNo;

    /**
     * 团状态
     */
    private String groupStatus;

    /**
     * 拼团参与状态
     */
    private String orderStatus;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员头像
     */
    private String memberAvatar;

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 拼团活动名称
     */
    private String activityName;

    /**
     * 拼团商品名称
     */
    private String productName;

    /**
     * 拼团商品主图
     */
    private String productImage;

    /**
     * 拼团价格
     */
    private BigDecimal groupPrice;

    /**
     * 优惠后应付金额
     */
    private BigDecimal actualAmount;

    /**
     * 支付成功时间
     */
    private LocalDateTime payTime;

    /**
     * 是否团长订单
     */
    private Boolean isLeader;

    /**
     * 所在团成团人数
     */
    private Integer groupMinPeople;

    /**
     * 所在团已支付成员数
     */
    private Integer groupMemberCount;

    /**
     * 所在团已占席成员数
     */
    private Integer groupReservedMemberCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
