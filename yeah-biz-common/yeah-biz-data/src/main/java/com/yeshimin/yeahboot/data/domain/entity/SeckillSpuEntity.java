package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀商品SPU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_seckill_spu")
public class SeckillSpuEntity extends ConditionBaseEntity<SeckillSpuEntity> {

    /**
     * 商户ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 原始SPU ID
     */
    private Long spuId;

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 销量
     */
    private Long sales;

    /**
     * 主图
     */
    private String mainImage;

    /**
     * 商品详细描述
     */
    private String detailDesc;

    /**
     * 审核状态，见枚举：SeckillActivityEnrollAuditStatusEnum
     */
    private Integer auditStatus;
}
