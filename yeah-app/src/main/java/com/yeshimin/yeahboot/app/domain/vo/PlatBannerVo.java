package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatBannerVo extends BaseDomain {

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 跳转目标，为空时不跳转
     */
    private String target;
}
