package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.PointsOrderEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * App积分订单详情视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointsOrderDetailVo extends PointsOrderEntity {

    /**
     * 物流公司名称
     */
    private String deliveryProviderName;
}
