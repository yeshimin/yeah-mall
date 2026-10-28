package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

/**
 * 店铺商品数量视图
 */
@Data
public class ShopProductCountVo {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商品数量
     */
    private Long productCount;
}
