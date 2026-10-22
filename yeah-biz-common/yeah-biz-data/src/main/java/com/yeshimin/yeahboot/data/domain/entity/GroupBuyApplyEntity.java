package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拼团活动报名申请表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_group_buy_apply")
public class GroupBuyApplyEntity extends ShopConditionBaseEntity<GroupBuyApplyEntity> {

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 报名的拼团商品SPU ID
     */
    private Long groupBuySpuId;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 申请备注
     */
    private String applyRemark;

    /**
     * 审核备注
     */
    private String auditRemark;
}
