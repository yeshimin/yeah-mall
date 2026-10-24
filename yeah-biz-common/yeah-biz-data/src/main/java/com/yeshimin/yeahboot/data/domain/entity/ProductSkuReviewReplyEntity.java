package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品评价公开回复表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_sku_review_reply")
public class ProductSkuReviewReplyEntity extends ShopConditionBaseEntity<ProductSkuReviewReplyEntity> {

    /**
     * 评价ID
     */
    private Long reviewId;

    /**
     * 上一条回复ID，0表示直接回复首评
     */
    private Long parentId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 回复方类型：1买家，2商家
     */
    private Integer senderType;

    /**
     * 回复操作人ID
     */
    private Long senderId;

    /**
     * 回复内容
     */
    private String content;
}
