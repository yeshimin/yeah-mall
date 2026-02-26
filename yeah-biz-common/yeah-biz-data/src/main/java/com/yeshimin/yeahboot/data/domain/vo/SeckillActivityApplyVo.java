package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀活动报名申请VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivityApplyVo extends BaseDomain {

    /**
     * 申请ID
     */
    private Long applyId;

    /**
     * 商户ID
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
     * 秒杀商品SPU ID
     */
    private Long seckillSpuId;

    /**
     * 商品名称
     */
    private String spuName;

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
