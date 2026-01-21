package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderDeliveryTrackingEntity;
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
     * 2.上次成功查询时间已经过去25分钟（时间要job执行频率，以防边界问题）
     */
    public List<OrderDeliveryTrackingEntity> findListForQuery() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(25);
        return lambdaQuery()
                .eq(OrderDeliveryTrackingEntity::getStatusLocked, false)
                .le(OrderDeliveryTrackingEntity::getLastSuccessQueryTime, thresholdTime)
                .list();
    }
}
