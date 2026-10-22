package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拼团订单关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_group_buy_order")
public class GroupBuyOrderEntity extends ShopConditionBaseEntity<GroupBuyOrderEntity> {

    /**
     * 团ID
     */
    private Long groupId;

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 拼团商品SPU ID
     */
    private Long groupBuySpuId;

    /**
     * 拼团商品SKU ID
     */
    private Long groupBuySkuId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 是否团长
     */
    private Boolean isLeader;

    /**
     * 参与状态
     */
    private String status;
}
