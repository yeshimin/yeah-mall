package com.yeshimin.yeahboot.app.auth;

import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 绑定微信身份请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxIdentityBindDto extends BaseDomain {

    /**
     * 微信小程序临时登录凭证
     */
    @NotBlank(message = "微信登录凭证不能为空")
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String code;
}
