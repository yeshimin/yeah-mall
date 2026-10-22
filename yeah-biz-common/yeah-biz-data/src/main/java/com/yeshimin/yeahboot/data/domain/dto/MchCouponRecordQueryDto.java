package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家优惠券领取和使用记录查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchCouponRecordQueryDto extends BaseQueryDto {

    /**
     * 商家优惠券ID
     */
    private Long couponId;

    /**
     * 会员昵称、账号或手机号
     */
    private String memberKeyword;

    /**
     * 状态：0-未使用，1-已使用，2-已过期
     */
    private Integer status;
}
