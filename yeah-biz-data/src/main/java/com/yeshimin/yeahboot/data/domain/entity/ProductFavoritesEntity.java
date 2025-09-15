package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品收藏表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_favorites")
public class ProductFavoritesEntity extends ShopConditionBaseEntity<ProductFavoritesEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 商品SPU ID
     */
    private Long spuId;
}
