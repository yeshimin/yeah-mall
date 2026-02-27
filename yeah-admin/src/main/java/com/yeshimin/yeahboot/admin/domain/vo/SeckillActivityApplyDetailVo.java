package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 秒杀活动报名申请详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivityApplyDetailVo extends BaseDomain {

    /**
     * 秒杀活动报名申请记录
     */
    private SeckillActivityApplyEntity apply;

    /**
     * 秒杀spu信息
     */
    private SeckillSpuEntity spu;

    /**
     * 秒杀spu轮播图片信息
     */
    private List<String> images;

    /**
     * 秒杀sku信息
     */
    private List<SeckillSkuEntity> skuList;
}
