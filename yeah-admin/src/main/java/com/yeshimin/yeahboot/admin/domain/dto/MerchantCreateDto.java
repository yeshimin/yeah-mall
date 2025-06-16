package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantCreateDto extends BaseDomain {

    /**
     * 登录账号
     */
    @NotBlank(message = "登录账号不能为空")
    private String loginAccount;

    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码不能为空")
    private String loginPassword;
}
