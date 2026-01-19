package com.yeshimin.yeahboot.auth.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JwtPayloadVo extends BaseDomain {

    private Integer iat;
    private Long iatMs;

    // 真正的过期时间由redis过期机制控制，这里只是token创建时所计算的过期时间，不是【刷新token过期时间】后新的过期时间
    private Integer exp;
    private Long expMs;

    private String aud;

    private String sub;

    private String term;
}
