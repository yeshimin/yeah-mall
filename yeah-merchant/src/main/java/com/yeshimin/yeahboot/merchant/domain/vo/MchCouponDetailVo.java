package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MchCouponDetailVo extends BaseDomain {

    /**
     * 优惠券信息
     */
    private MchCouponEntity coupon;
}