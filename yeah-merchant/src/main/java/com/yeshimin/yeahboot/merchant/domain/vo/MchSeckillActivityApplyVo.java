package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商家端-秒杀活动报名申请详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchSeckillActivityApplyVo extends BaseDomain {

    /**
     * ID
     */
    private Long id;

    /**
     * 商家ID
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
