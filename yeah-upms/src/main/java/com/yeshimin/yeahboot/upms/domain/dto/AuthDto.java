package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthDto extends BaseDomain {

    @NotBlank(message = "凭证不能为空")
    private String token;

    private Boolean onlyAuthenticate = false;
}
