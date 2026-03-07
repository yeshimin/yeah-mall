package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 秒杀活动商品SPU VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillSpuVo extends BaseDomain {

    /**
     * ID
     */
    private Long id;

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 商家名称
     */
    private String mchName;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 原始SPU ID
     */
    private Long spuId;

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 秒杀活动名称
     */
    private String activityName;

    /**
     * 秒杀场次ID
     */
    private Long sessionId;

    /**
     * 秒杀活动场次名称
     */
    private String sessionName;

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
     * sku最低价-原价
     */
    private BigDecimal minOriginPrice;

    /**
     * sku最低价-秒杀价
     */
    private BigDecimal minSeckillPrice;

    /**
     * sku最高价-原价
     */
    private BigDecimal maxOriginPrice;

    /**
     * sku最高价-秒杀价
     */
    private BigDecimal maxSeckillPrice;

    /**
     * 商品详细描述
     */
    private String detailDesc;

    /**
     * 审核状态，见枚举：SeckillActivityApplyAuditStatusEnum
     */
    private Integer auditStatus;
}
