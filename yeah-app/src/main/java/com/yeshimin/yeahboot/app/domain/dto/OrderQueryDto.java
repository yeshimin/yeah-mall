package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.app.common.enums.OrderStatusEnum;
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
     * 订单状态：同订单表
     */
    @EnumValue(enumClass = OrderStatusEnum.class, message = "订单状态不正确")
    private String status;
}
