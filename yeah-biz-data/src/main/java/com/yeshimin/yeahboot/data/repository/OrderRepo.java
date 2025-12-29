package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class OrderRepo extends BaseRepo<OrderMapper, OrderEntity> {

    /**
     * findOneByOrderNo
     */
    public OrderEntity findOneByOrderNo(String orderNo) {
        return lambdaQuery().eq(OrderEntity::getOrderNo, orderNo).one();
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
}
