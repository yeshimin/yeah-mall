package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderReviewEntity;
import com.yeshimin.yeahboot.data.mapper.OrderReviewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderReviewRepo extends BaseRepo<OrderReviewMapper, OrderReviewEntity> {

    /**
     * createOne
     */
    public OrderReviewEntity createOne(OrderEntity order, Integer descriptionRating,
                                       Integer serviceRating, Integer deliveryRating) {
        OrderReviewEntity review = new OrderReviewEntity();
        review.setDescriptionRating(descriptionRating);
        review.setServiceRating(serviceRating);
        review.setDeliveryRating(deliveryRating);
        // 归属信息
        review.setMchId(order.getMchId());
        review.setShopId(order.getShopId());
        review.setMemberId(order.getMemberId());
        review.setOrderId(order.getId());
        review.setOrderNo(order.getOrderNo());
        review.insert();
        return review;
    }
}
