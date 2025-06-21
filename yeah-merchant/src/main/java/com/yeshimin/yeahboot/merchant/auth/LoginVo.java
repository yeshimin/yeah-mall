package com.yeshimin.yeahboot.merchant.auth;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录-VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginVo extends BaseDomain {

    /**
     * Token
     */
    private String token;
}
