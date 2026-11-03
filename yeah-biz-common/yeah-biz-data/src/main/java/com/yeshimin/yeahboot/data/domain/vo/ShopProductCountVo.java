package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

/**
 * 店铺在售商品数量视图
 */
@Data
public class ShopProductCountVo {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 在售商品数量
     */
    private Long productCount;
}
