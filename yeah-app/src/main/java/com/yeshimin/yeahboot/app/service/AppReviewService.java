package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.dto.ReviewPublishDto;
import com.yeshimin.yeahboot.app.domain.dto.ReviewPublishItemDto;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppReviewService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final OrderReviewRepo orderReviewRepo;
    private final ProductSkuReviewRepo productSkuReviewRepo;
    private final ProductSkuReviewImageRepo productSkuReviewImageRepo;

    private final StorageManager storageManager;

    /**
     * 发布评价
     */
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long userId, ReviewPublishDto dto) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(dto.getOrderId(), "订单不存在");
        // 检查：订单是否属于当前用户
        if (!order.getMemberId().equals(userId)) {
            throw new BaseException("订单不属于当前用户");
        }
        // 检查：订单是否为可评价状态
        if (!OrderStatusEnum.isReviewable(order.getStatus())) {
            throw new BaseException("订单不可评价");
        }
        // 检查：订单是否已评价
        if (order.getIsReviewed()) {
            throw new BaseException("订单已评价");
        }
        // 检查：明细ID是否正确
        Set<Long> itemIds = dto.getItems().stream()
                .map(ReviewPublishItemDto::getOrderItemId).collect(Collectors.toSet());
        Set<Long> itemIds0 = orderItemRepo.findIdSetByOrderId(order.getId());
        if (!itemIds.equals(itemIds0)) {
            throw new BaseException("订单明细ID错误");
        }

        // 订单更新为已评价
        boolean updateReviewed = orderRepo.updateIsReviewed(order.getId());
        if (!updateReviewed) {
            throw new BaseException("订单更新为已评价失败");
        }

        // 创建订单评价记录
        orderReviewRepo.createOne(order, dto.getDescriptionRating(), dto.getDeliveryRating(), dto.getServiceRating());

        // 创建商品SKU评价记录
        for (ReviewPublishItemDto item : dto.getItems()) {
            // 检查：订单明细ID
            OrderItemEntity orderItem = orderItemRepo.getOneById(item.getOrderItemId(), "订单明细不存在");
            // 检查：标记使用图片
            storageManager.markUse(item.getImages().toArray(new String[0]));

            ProductSkuReviewEntity skuReview = productSkuReviewRepo.createOne(orderItem, item.getOverallRating(),
                    item.getContent(), dto.getIsAnonymous(), order.getMemberNickname(), order.getMemberAvatar());
            // 创建评价图片记录
            productSkuReviewImageRepo.saveImage(skuReview, item.getImages());
        }
    }
}
