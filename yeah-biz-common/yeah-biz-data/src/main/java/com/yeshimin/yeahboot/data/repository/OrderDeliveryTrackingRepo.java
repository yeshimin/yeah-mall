package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderDeliveryTrackingEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.mapper.OrderDeliveryTrackingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class OrderDeliveryTrackingRepo extends BaseRepo<OrderDeliveryTrackingMapper, OrderDeliveryTrackingEntity> {

    /**
     * findListForQuery
     * 同时满足以下条件：
     * 1.状态未锁定
     * 2.30天以内的数据
     */
    public List<OrderDeliveryTrackingEntity> findListForQuery() {
        return lambdaQuery()
                .eq(OrderDeliveryTrackingEntity::getStatusLocked, false)
                .ge(OrderDeliveryTrackingEntity::getCreateTime, LocalDateTime.now().minusDays(30))
                .list();
    }

    /**
     * findOneByOrderNo
     */
    public OrderDeliveryTrackingEntity findOneByOrderNo(String orderNo) {
        return lambdaQuery()
                .eq(OrderDeliveryTrackingEntity::getOrderNo, orderNo)
                .orderByDesc(OrderDeliveryTrackingEntity::getCreateTime)
                .last("LIMIT 1")
                .one();
    }

    /**
     * createOne
     */
    public boolean createOne(OrderEntity order, String trackingNo, String trackingCom) {
        OrderDeliveryTrackingEntity entity = new OrderDeliveryTrackingEntity();
        entity.setMchId(order.getMchId());
        entity.setShopId(order.getShopId());
        entity.setOrderId(order.getId());
        entity.setOrderNo(order.getOrderNo());
        entity.setTrackingNo(trackingNo);
        entity.setTrackingCom(trackingCom);
        return super.save(entity);
    }
}
