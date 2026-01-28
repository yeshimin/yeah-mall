package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /**
     * 更新订单状态
     * 场景：取消订单
     */
    public boolean updateStatus2(Long orderId, String statusFrom, String statusTo, LocalDateTime closeTime, String closeReason) {
        int count = orderMapper.updateOrderStatus(orderId, statusFrom, statusTo);
        if (count == 0) {
            return false;
        }

        LambdaUpdateWrapper<OrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderEntity::getId, orderId)
                .set(OrderEntity::getCloseTime, closeTime)
                .set(OrderEntity::getCloseReason, closeReason);
        super.update(updateWrapper);

        return true;
    }

    /**
     * 更新退款状态
     */
    public boolean updateRefundStatus(Long orderId, String statusFrom, String statusTo) {
        int count = orderMapper.updateRefundStatus(orderId, statusFrom, statusTo);
        return count > 0;
    }

    /**
     * 更新退款状态
     */
    public boolean updateRefundStatus2(Long orderId, String refundStatus, String summary, LocalDateTime successTime) {
        LambdaUpdateWrapper<OrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderEntity::getId, orderId)
                // 仅当订单状态为【退款】时才更新退款子状态
                .eq(OrderEntity::getStatus, OrderStatusEnum.REFUND.getValue())
                .set(OrderEntity::getRefundStatus, refundStatus)
                .set(OrderEntity::getRefundSummary, summary)
                .set(successTime != null, OrderEntity::getPaySuccessTime, successTime);
        return super.update(updateWrapper);
    }

    /**
     * findIdListForPayExpired
     * 匹配条件：
     * 1.订单状态为【待付款】
     * 2.已经到达付款超时时间
     */
    public List<OrderEntity> findIdListForPayExpired() {
        return lambdaQuery().select(OrderEntity::getId)
                .eq(OrderEntity::getStatus, OrderStatusEnum.WAIT_PAY.getValue())
                .le(OrderEntity::getPayExpireTime, LocalDateTime.now())
                .list();
    }

    /**
     * findIdListForReceiveExpired
     * 自动确认收货
     * 匹配条件：
     * 1.订单状态为【待收货】
     * 2.已经到达签收超时时间
     */
    public List<OrderEntity> findIdListForReceiveExpired() {
        return lambdaQuery().select(OrderEntity::getId)
                .eq(OrderEntity::getStatus, OrderStatusEnum.WAIT_RECEIVE.getValue())
                .le(OrderEntity::getReceiveExpireTime, LocalDateTime.now())
                .list();
    }
}
