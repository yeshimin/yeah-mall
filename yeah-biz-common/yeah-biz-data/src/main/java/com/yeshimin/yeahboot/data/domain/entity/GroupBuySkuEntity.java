package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 拼团商品SKU表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_group_buy_sku")
public class GroupBuySkuEntity extends ShopConditionBaseEntity<GroupBuySkuEntity> {

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 原始商品SKU ID
     */
    private Long skuId;

    /**
     * 拼团商品SPU ID
     */
    private Long groupBuySpuId;

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 名称
     */
    private String name;

    /**
     * 规格编码
     */
    private String specCode;

    /**
     * 原始价格
     */
    private BigDecimal originPrice;

    /**
     * 拼团价格
     */
    private BigDecimal groupPrice;

    /**
     * 拼团库存
     */
    private Integer stock;

    /**
     * 是否已从源SKU预留库存
     */
    private Boolean isStockReserved;

    /**
     * 自定义主图，为空时使用原始SKU主图
     */
    private String mainImage;
}
