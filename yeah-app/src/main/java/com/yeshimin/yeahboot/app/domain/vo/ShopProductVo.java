package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 店铺商品
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopProductVo extends BaseDomain {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;
}
