package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商家端-秒杀活动报名申请详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchSeckillActivityApplyDetailVo extends BaseDomain {

    /**
     * 秒杀活动报名申请记录
     */
    private MchSeckillActivityApplyVo apply;

    /**
     * 秒杀spu信息
     */
    private MchSeckillSpuVo spu;

    /**
     * 秒杀spu轮播图片信息
     */
    private List<String> images;

    /**
     * 秒杀sku信息
     */
    private List<MchSeckillSkuVo> skuList;
}
