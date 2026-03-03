package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理端-秒杀活动报名申请详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminSeckillSpuVo extends BaseDomain {

    /**
     * ID
     */
    private Long id;

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
     * 审核状态，见枚举：SeckillActivityApplyAuditStatusEnum
     */
    private Integer auditStatus;
}
