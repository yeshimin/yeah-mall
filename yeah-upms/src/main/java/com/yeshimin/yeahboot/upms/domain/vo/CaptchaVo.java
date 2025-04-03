package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图形验证码VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class CaptchaVo extends BaseDomain {

    /**
     * 验证码key
     */
    private String key;

    /**
     * 验证码图片
     */
    private String image;
}
