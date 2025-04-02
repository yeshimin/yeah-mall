package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
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

    /**
     * 用户名
     */
    private String username;
}
