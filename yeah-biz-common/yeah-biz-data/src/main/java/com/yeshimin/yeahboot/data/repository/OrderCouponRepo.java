package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.mapper.OrderCouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderCouponRepo extends BaseRepo<OrderCouponMapper, OrderCouponEntity> {

    /**
     * findOneByOrderId
     */
    public OrderCouponEntity findOneByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderCouponEntity::getOrderId, orderId).one();
    }

    /**
     * createOne
     */
    public OrderCouponEntity createOne(OrderEntity order, MemberCouponEntity memberCoupon) {
        OrderCouponEntity orderCoupon = new OrderCouponEntity();
        orderCoupon.setMchId(order.getMchId());
        orderCoupon.setShopId(order.getShopId());
        orderCoupon.setOrderId(order.getId());
        orderCoupon.setCouponId(memberCoupon.getId());
        orderCoupon.setName(memberCoupon.getName());
        orderCoupon.setDescription(memberCoupon.getDescription());
        orderCoupon.setType(memberCoupon.getType());
        orderCoupon.setAmount(memberCoupon.getAmount());
        orderCoupon.setDiscount(memberCoupon.getDiscount());
        orderCoupon.setMinAmount(memberCoupon.getMinAmount());
        orderCoupon.setUseRange(memberCoupon.getUseRange());
        orderCoupon.setTargetId(memberCoupon.getTargetId());
        orderCoupon.setBeginTime(memberCoupon.getBeginTime());
        orderCoupon.setEndTime(memberCoupon.getEndTime());
        super.save(orderCoupon);
        return orderCoupon;
    }
}
