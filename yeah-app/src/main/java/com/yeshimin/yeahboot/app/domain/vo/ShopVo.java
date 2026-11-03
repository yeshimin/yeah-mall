package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopVo extends BaseDomain {

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 平台是否启用
     */
    private Boolean isEnabled;

    /**
     * 商家是否营业
     */
    private Boolean isOpen;
}
