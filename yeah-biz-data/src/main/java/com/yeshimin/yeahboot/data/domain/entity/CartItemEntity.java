package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车商品项表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_cart_item")
public class CartItemEntity extends ConditionBaseEntity<CartItemEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 商户ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 商品SKU ID
     */
    private Long skuId;
}
