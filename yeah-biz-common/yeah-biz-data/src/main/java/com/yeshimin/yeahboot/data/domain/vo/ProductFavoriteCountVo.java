package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

/**
 * 商品收藏数量视图
 */
@Data
public class ProductFavoriteCountVo {

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 收藏人数
     */
    private Long favoriteCount;
}
