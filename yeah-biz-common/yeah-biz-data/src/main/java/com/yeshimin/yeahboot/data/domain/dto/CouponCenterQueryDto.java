package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.common.enums.CouponUseRangeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * app端领券中心查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponCenterQueryDto extends BaseDomain {

    /**
     * 使用范围：1-店铺通用 2-指定商品 3-指定分类
     */
    @EnumValue(enumClass = CouponUseRangeEnum.class)
    private Integer useRange;
}
