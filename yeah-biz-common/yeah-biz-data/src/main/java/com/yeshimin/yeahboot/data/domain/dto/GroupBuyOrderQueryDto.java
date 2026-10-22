package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
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
    private String status;

    /**
     * 拼团状态
     */
    private String groupStatus;
}
