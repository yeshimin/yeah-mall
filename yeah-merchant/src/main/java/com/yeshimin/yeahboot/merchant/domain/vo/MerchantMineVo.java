package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商家登录后要用到的信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantMineVo extends BaseDomain {

    private MerchantEntity user;

    private List<Object> roles;

    private List<Object> orgs;

    private List<String> permissions;
}
