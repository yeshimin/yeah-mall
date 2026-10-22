package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 拼团商品SPU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_group_buy_spu")
public class GroupBuySpuEntity extends ShopConditionBaseEntity<GroupBuySpuEntity> {

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 销量
     */
    private Long sales;

    /**
     * SKU最低原价
     */
    private BigDecimal minOriginPrice;

    /**
     * SKU最低拼团价
     */
    private BigDecimal minGroupPrice;

    /**
     * SKU最高原价
     */
    private BigDecimal maxOriginPrice;

    /**
     * SKU最高拼团价
     */
    private BigDecimal maxGroupPrice;

    /**
     * 自定义主图，为空时使用原始SPU主图
     */
    private String mainImage;

    /**
     * 商品详细描述
     */
    private String detailDesc;

    /**
     * 审核状态
     */
    private Integer auditStatus;
}
