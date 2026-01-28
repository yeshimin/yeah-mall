package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.common.enums.WxRefundStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.mapper.OrderRefundMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Repository
public class OrderRefundRepo extends BaseRepo<OrderRefundMapper, OrderRefundEntity> {

    /**
     * createOne
     */
    public boolean createOne(OrderEntity order, String refundNo,
                             BigDecimal totalAmount, BigDecimal refundAmount, String refundReason) {
        OrderRefundEntity orderRefund = new OrderRefundEntity();
        orderRefund.setMchId(order.getMchId());
        orderRefund.setShopId(order.getShopId());
        orderRefund.setMemberId(order.getMemberId());
        orderRefund.setOrderId(order.getId());
        orderRefund.setOrderNo(order.getOrderNo());
        orderRefund.setRefundNo(refundNo);
        orderRefund.setTotalAmount(totalAmount);
        orderRefund.setRefundAmount(refundAmount);
        orderRefund.setRefundStatus(WxRefundStatusEnum.PROCESSING.getValue());
        orderRefund.setRefundReason(refundReason);
        boolean r = orderRefund.insert();
        log.debug("orderRefund.createOne: {}", r);
        return r;
    }

    /**
     * findOneByRefundNo
     */
    public OrderRefundEntity findOneByRefundNo(String refundNo) {
        return lambdaQuery().eq(OrderRefundEntity::getRefundNo, refundNo).one();
    }

    /**
     * findListByOrderId
     */
    public List<OrderRefundEntity> findListByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderRefundEntity::getOrderId, orderId).list();
    }
}
