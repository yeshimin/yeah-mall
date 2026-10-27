package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.common.enums.PointsFulfillmentTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.PointsOrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderEntity;
import com.yeshimin.yeahboot.data.mapper.PointsOrderMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class PointsOrderRepo extends BaseRepo<PointsOrderMapper, PointsOrderEntity> {

    public boolean cancelPending(Long id, LocalDateTime cancelTime, String cancelReason) {
        return baseMapper.cancelPending(id, cancelTime, cancelReason,
                PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue(),
                PointsOrderStatusEnum.CANCELED.getValue()) > 0;
    }

    public boolean shipPending(Long id, String providerCode, String trackingNo, LocalDateTime shipTime) {
        return baseMapper.shipPending(id, providerCode, trackingNo, shipTime,
                PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue(),
                PointsOrderStatusEnum.SHIPPED.getValue(),
                PointsFulfillmentTypeEnum.LOGISTICS.getValue()) > 0;
    }

    public boolean completePending(Long id, LocalDateTime completeTime, String fulfillmentRemark) {
        return baseMapper.completePending(id, completeTime, fulfillmentRemark,
                PointsOrderStatusEnum.PENDING_FULFILLMENT.getValue(),
                PointsOrderStatusEnum.COMPLETED.getValue()) > 0;
    }

    public boolean completeShipped(Long id, LocalDateTime completeTime) {
        return baseMapper.completeShipped(id, completeTime,
                PointsOrderStatusEnum.SHIPPED.getValue(),
                PointsOrderStatusEnum.COMPLETED.getValue()) > 0;
    }

    public long sumExchangedQuantity(Long memberId, Long spuId) {
        Long result = baseMapper.sumExchangedQuantity(memberId, spuId, PointsOrderStatusEnum.CANCELED.getValue());
        return result == null ? 0L : result;
    }
}
