package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端-秒杀活动商品SPU详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminSeckillSpuDetailVo extends BaseDomain {

    /**
     * 秒杀spu信息
     */
    private AdminSeckillSpuVo spu;

    /**
     * 秒杀spu轮播图片信息
     */
    private List<String> images;

    /**
     * 秒杀sku信息
     */
    private List<AdminSeckillSkuVo> skuList;

    /**
     * 报名时间
     */
    private LocalDateTime applyTime;

    /**
     * 审核时间（待审核时为空）
     */
    private LocalDateTime auditTime;

    /**
     * 报名备注
     */
    private String applyRemark;

    /**
     * 审核备注
     */
    private String auditRemark;
}
