package com.yeshimin.yeahboot.domain.dto;

import com.yeshimin.yeahboot.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthenticateDto extends BaseDomain {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}