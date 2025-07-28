package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品sku规格配置VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSkuSpecVo extends BaseDomain {

    /**
     * 规格ID
     */
    private Long specId;

    /**
     * 选项ID
     */
    private Long optId;
}
