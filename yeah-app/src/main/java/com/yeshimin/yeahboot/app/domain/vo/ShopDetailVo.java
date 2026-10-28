package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 店铺详情
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopDetailVo extends BaseDomain {

    /**
     * 店铺ID
     */
    private Long id;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺Logo
     */
    private String shopLogo;

    /**
     * 店铺商品数量
     */
    private Long productCount;
}
