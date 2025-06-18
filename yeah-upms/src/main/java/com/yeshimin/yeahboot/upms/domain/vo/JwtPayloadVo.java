package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JwtPayloadVo extends BaseDomain {

    private Integer iat;

    private Integer exp;

    private String aud;

    private String sub;

    private String term;
}
