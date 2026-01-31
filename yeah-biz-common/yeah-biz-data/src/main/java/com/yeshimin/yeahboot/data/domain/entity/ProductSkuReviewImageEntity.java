package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SKU评价图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_sku_review_image")
public class ProductSkuReviewImageEntity extends ShopConditionBaseEntity<ProductSkuReviewImageEntity> {

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
     * SPU ID
     */
    private Long spuId;

    /**
     * 评价ID
     */
    private Long reviewId;

    /**
     * 图片
     */
    private String image;
}
