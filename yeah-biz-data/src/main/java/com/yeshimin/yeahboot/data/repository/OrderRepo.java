package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepo extends BaseRepo<OrderMapper, OrderEntity> {

    private final OrderMapper orderMapper;

    /**
     * findOneByOrderNo
     */
    public OrderEntity findOneByOrderNo(String orderNo) {
        return lambdaQuery().eq(OrderEntity::getOrderNo, orderNo).one();
    }

    /**
     * countByOrderNo
     */
    public long countByOrderNo(String orderNo) {
        return lambdaQuery().eq(OrderEntity::getOrderNo, orderNo).count();
    }

    /**
     * countByStatus
     */
    public long countByStatus(Long userId, String status) {
        return lambdaQuery().eq(OrderEntity::getMemberId, userId).eq(OrderEntity::getStatus, status).count();
    }

    /**
     * countByMemberIdAndStatusList
     */
    public long countByMemberIdAndStatusList(Long memberId, List<String> statusList) {
        return lambdaQuery().eq(OrderEntity::getMemberId, memberId).in(OrderEntity::getStatus, statusList).count();
    }

    /**
     * 更新订单状态
     */
    public boolean updateStatus(Long orderId, String statusFrom, String statusTo) {
        int count = orderMapper.updateOrderStatus(orderId, statusFrom, statusTo);
        return count > 0;
    }
}
