package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.data.mapper.OrderItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class OrderItemRepo extends BaseRepo<OrderItemMapper, OrderItemEntity> {

    /**
     * findListByOrderIds
     */
    public List<OrderItemEntity> findListByOrderIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery().in(OrderItemEntity::getOrderId, orderIds).list();
    }

    /**
     * findListByOrderId
     */
    public List<OrderItemEntity> findListByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderItemEntity::getOrderId, orderId).list();
    }

    /**
     * findIdSetByOrderId
     */
    public Set<Long> findIdSetByOrderId(Long orderId) {
        return super.lambdaQuery().eq(OrderItemEntity::getOrderId, orderId).select(OrderItemEntity::getId)
                .list().stream().map(OrderItemEntity::getId).collect(Collectors.toSet());
    }
}
