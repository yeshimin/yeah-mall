package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀活动报名申请表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_seckill_activity_apply")
public class SeckillActivityApplyEntity extends ConditionBaseEntity<SeckillActivityApplyEntity> {

    /**
     * 商户ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 报名的秒杀商品SPU ID
     */
    private Long seckillSpuId;

    /**
     * 审核状态，见枚举：SeckillActivityApplyAuditStatusEnum
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
