package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商家重置密码参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantResetPasswordDto extends BaseDomain {

    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Long id;

    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码不能为空")
    private String loginPassword;
}
