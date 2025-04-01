package com.yeshimin.yeahboot.domain.dto;

import com.yeshimin.yeahboot.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 登录-DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginDto extends BaseDomain {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
