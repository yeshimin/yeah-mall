package com.yeshimin.yeahboot.admin.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商家详情视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantDetailVo extends MerchantListVo {

    /**
     * 关联店铺
     */
    private List<MerchantShopVo> shops;
}
