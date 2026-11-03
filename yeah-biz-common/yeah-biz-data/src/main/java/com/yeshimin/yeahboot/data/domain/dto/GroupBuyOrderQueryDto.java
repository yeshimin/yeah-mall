package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyGroupStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyOrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拼团订单查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyOrderQueryDto extends BaseQueryDto {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 拼团参与订单状态
     */
    @EnumValue(enumClass = GroupBuyOrderStatusEnum.class, message = "拼团订单状态不正确")
    private String status;

    /**
     * 拼团状态
     */
    @EnumValue(enumClass = GroupBuyGroupStatusEnum.class, message = "拼团状态不正确")
    private String groupStatus;
}
