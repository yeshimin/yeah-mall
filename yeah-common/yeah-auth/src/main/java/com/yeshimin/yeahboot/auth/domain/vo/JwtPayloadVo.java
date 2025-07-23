package com.yeshimin.yeahboot.auth.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JwtPayloadVo extends BaseDomain {

    private Integer iat;

    private Long iatMs;

    private Integer exp;

    private Long expMs;

    private String aud;

    private String sub;

    private String term;
}
