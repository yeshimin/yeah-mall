package com.yeshimin.yeahboot.domain.vo;

import com.yeshimin.yeahboot.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthenticateVo extends BaseDomain {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;
}
