package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.common.enums.MemberCouponQueryConditionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * app端领券中心查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponQueryDto extends BaseDomain {

    /**
     * 条件：1-可用 2-已使用 3-已过期
     */
    @NotNull(message = "条件不能为空")
    @EnumValue(enumClass = MemberCouponQueryConditionEnum.class)
    private Integer condition;

    // of
    public static CouponQueryDto of(Integer condition) {
        CouponQueryDto dto = new CouponQueryDto();
        dto.setCondition(condition);
        return dto;
    }
}
