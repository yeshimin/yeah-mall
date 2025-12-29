package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.app.common.enums.OrderAggreStatusEnum;
import com.yeshimin.yeahboot.common.common.config.mybatis.Query;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class OrderQueryDto extends BaseQueryDto {

    /**
     * 订单聚合状态
     */
    @EnumValue(enumClass = OrderAggreStatusEnum.class, message = "订单状态不正确")
    private String aggreStatus;
}
