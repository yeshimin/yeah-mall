package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 店铺Banner
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MallShopBannerVo extends BaseDomain {

    /**
     * Banner ID
     */
    private Long id;

    /**
     * 图片文件Key
     */
    private String imageUrl;

    /**
     * 跳转目标，为空时不跳转
     */
    private String target;
}
