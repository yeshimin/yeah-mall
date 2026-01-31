package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SKU评价表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_sku_review")
public class ProductSkuReviewEntity extends ShopConditionBaseEntity<ProductSkuReviewEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU name
     */
    private String skuName;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SPU name
     */
    private String spuName;

    /**
     * SPU主图
     */
    private String spuMainImage;

    /**
     * 综合评价：1-5
     */
    private Integer overallRating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * 买家昵称
     */
    private String nickname;

    /**
     * 买家头像
     */
    private String avatar;
}
