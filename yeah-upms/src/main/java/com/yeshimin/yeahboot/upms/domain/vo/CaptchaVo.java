package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图形验证码VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CaptchaVo extends BaseDomain {

    /**
     * 验证码key
     */
    private String key;

    /**
     * 验证码图片
     */
    private String image;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
