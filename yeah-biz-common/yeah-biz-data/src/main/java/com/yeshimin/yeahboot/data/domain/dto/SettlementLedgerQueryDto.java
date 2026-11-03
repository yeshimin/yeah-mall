package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.common.enums.SettlementLedgerStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家结算台账查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementLedgerQueryDto extends BaseDomain {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 台账状态
     */
    @EnumValue(enumClass = SettlementLedgerStatusEnum.class, message = "台账状态不正确")
    private String status;

    /**
     * 结算批次编号
     */
    private String settlementNo;
}
