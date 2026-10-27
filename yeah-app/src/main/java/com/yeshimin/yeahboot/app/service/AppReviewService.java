package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.ReviewPublishDto;
import com.yeshimin.yeahboot.app.domain.dto.ReviewPublishItemDto;
import com.yeshimin.yeahboot.app.domain.vo.ProductReviewListVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductReviewSummaryVo;
import com.yeshimin.yeahboot.app.domain.vo.OrderReviewDetailVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.ReviewReplySenderTypeEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderReviewEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewReplyEntity;
import com.yeshimin.yeahboot.data.domain.dto.ReviewReplyDto;
import com.yeshimin.yeahboot.data.domain.vo.ReviewReplyVo;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.service.ReviewReplyService;
import com.yeshimin.yeahboot.service.MemberPointsService;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.RoundingMode;
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
    private final ProductSkuReviewReplyRepo productSkuReviewReplyRepo;
    private final ReviewReplyService reviewReplyService;
    private final MemberPointsService memberPointsService;

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
        if (Boolean.TRUE.equals(order.getIsReviewed())) {
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
        boolean updateReviewed = orderRepo.updateIsReviewedIfNotReviewed(order.getId());
        if (!updateReviewed) {
            throw new BaseException("订单已评价，请勿重复提交");
        }

        // 创建订单评价记录
        orderReviewRepo.createOne(order, dto.getDescriptionRating(), dto.getDeliveryRating(), dto.getServiceRating());

        // 创建商品SKU评价记录
        for (ReviewPublishItemDto item : dto.getItems()) {
            // 检查：订单明细ID
            OrderItemEntity orderItem = orderItemRepo.getOneById(item.getOrderItemId(), "订单明细不存在");
            // 检查：标记使用图片
            storageManager.markUse(item.getImages().toArray(new String[0]));

            ProductSkuReviewEntity skuReview = productSkuReviewRepo.createOne(order, orderItem, item.getOverallRating(),
                    item.getContent(), dto.getIsAnonymous(), order.getMemberNickname(), order.getMemberAvatar());
            // 创建评价图片记录
            productSkuReviewImageRepo.saveImage(skuReview, item.getImages());
        }
        // 评价奖励按订单ID幂等发放，多SKU评价仍只产生一笔会员积分流水。
        memberPointsService.grantForReview(order);
    }

    /**
     * 查询商品评价列表
     */
    public IPage<ProductReviewListVo> list(Long userId, Page page, Long spuId, Boolean hasImage, Boolean goodOnly) {
        // 先分页查询当前SPU下的评价主记录。
        Page<ProductSkuReviewEntity> pageReview = productSkuReviewRepo.queryPage(page, spuId, hasImage, goodOnly);

        // 取出当前页评价ID，后续批量查询图片，避免逐条查库。
        List<Long> reviewIds = pageReview.getRecords().stream()
                .map(ProductSkuReviewEntity::getId)
                .collect(Collectors.toList());

        // 将评价图片按 reviewId 分组，便于和主记录装配。
        Map<Long, List<String>> mapImages = productSkuReviewImageRepo.findListByReviewIds(reviewIds).stream()
                .collect(Collectors.groupingBy(ProductSkuReviewImageEntity::getReviewId,
                        Collectors.mapping(ProductSkuReviewImageEntity::getImage, Collectors.toList())));
        Map<Long, List<ProductSkuReviewReplyEntity>> replyMap = productSkuReviewReplyRepo.findListByReviewIds(reviewIds)
                .stream().collect(Collectors.groupingBy(ProductSkuReviewReplyEntity::getReviewId));

        // 将分页主记录转换成前端展示VO，并补上图片列表。
        return pageReview.convert(review -> toListVo(review, mapImages, replyMap, userId));
    }

    /**
     * 查询商品评价摘要。
     */
    public ProductReviewSummaryVo summary(Long userId, Long spuId) {
        long totalCount = productSkuReviewRepo.lambdaQuery()
                .eq(ProductSkuReviewEntity::getSpuId, spuId)
                .eq(ProductSkuReviewEntity::getIsVisible, true)
                .count();
        long goodCount = productSkuReviewRepo.lambdaQuery()
                .eq(ProductSkuReviewEntity::getSpuId, spuId)
                .eq(ProductSkuReviewEntity::getIsVisible, true)
                .ge(ProductSkuReviewEntity::getOverallRating, 4)
                .count();
        long imageCount = productSkuReviewRepo.queryPage(Page.of(1, 1), spuId, true, false).getTotal();
        ProductReviewSummaryVo result = new ProductReviewSummaryVo();
        result.setTotalCount(totalCount);
        result.setGoodCount(goodCount);
        result.setImageCount(imageCount);
        result.setGoodRate(totalCount == 0 ? 100
                : java.math.BigDecimal.valueOf(goodCount * 100)
                .divide(java.math.BigDecimal.valueOf(totalCount), 0, RoundingMode.HALF_UP).intValue());
        result.setTopReviews(list(userId, Page.of(1, 3), spuId, null, false).getRecords());
        return result;
    }

    /**
     * 买家回复商家评价回复。
     */
    public void reply(Long userId, ReviewReplyDto dto) {
        reviewReplyService.replyByMember(userId, dto);
    }

    /**
     * 查询当前用户的订单评价详情。
     */
    public OrderReviewDetailVo orderDetail(Long userId, Long orderId) {
        OrderEntity order = orderRepo.getOneById(orderId, "订单不存在");
        if (!order.getMemberId().equals(userId)) {
            throw new BaseException("订单不属于当前用户");
        }
        OrderReviewEntity orderReview = orderReviewRepo.lambdaQuery()
                .eq(OrderReviewEntity::getOrderId, orderId)
                .one();
        if (orderReview == null) {
            throw new BaseException("订单尚未评价");
        }
        List<ProductSkuReviewEntity> reviews = productSkuReviewRepo.lambdaQuery()
                .eq(ProductSkuReviewEntity::getOrderId, orderId)
                .list();
        Map<Long, List<String>> imageMap = productSkuReviewImageRepo.findListByReviewIds(reviews.stream()
                .map(ProductSkuReviewEntity::getId).collect(Collectors.toList())).stream()
                .collect(Collectors.groupingBy(ProductSkuReviewImageEntity::getReviewId,
                        Collectors.mapping(ProductSkuReviewImageEntity::getImage, Collectors.toList())));
        Map<Long, List<ProductSkuReviewReplyEntity>> replyMap = productSkuReviewReplyRepo.findListByReviewIds(reviews.stream()
                .map(ProductSkuReviewEntity::getId).collect(Collectors.toList())).stream()
                .collect(Collectors.groupingBy(ProductSkuReviewReplyEntity::getReviewId));
        OrderReviewDetailVo result = new OrderReviewDetailVo();
        result.setOrderNo(order.getOrderNo());
        result.setReviewTime(orderReview.getCreateTime());
        result.setDescriptionRating(orderReview.getDescriptionRating());
        result.setDeliveryRating(orderReview.getDeliveryRating());
        result.setServiceRating(orderReview.getServiceRating());
        result.setIsAnonymous(reviews.stream().findFirst().map(ProductSkuReviewEntity::getIsAnonymous).orElse(false));
        result.setItems(reviews.stream()
                .map(review -> toListVo(review, imageMap, replyMap, userId))
                .collect(Collectors.toList()));
        return result;
    }

    /**
     * 组装评价、图片和公开回复。
     */
    private ProductReviewListVo toListVo(ProductSkuReviewEntity review, Map<Long, List<String>> imageMap,
                                         Map<Long, List<ProductSkuReviewReplyEntity>> replyMap, Long userId) {
        ProductReviewListVo vo = BeanUtil.copyProperties(review, ProductReviewListVo.class);
        vo.setImages(imageMap.getOrDefault(review.getId(), Collections.emptyList()));
        List<ProductSkuReviewReplyEntity> replies = replyMap.getOrDefault(review.getId(), Collections.emptyList());
        vo.setReplies(replies.stream().map(reply -> BeanUtil.copyProperties(reply, ReviewReplyVo.class))
                .collect(Collectors.toList()));

        ProductSkuReviewReplyEntity latestReply = replies.isEmpty() ? null : replies.get(replies.size() - 1);
        boolean isReviewOwner = userId != null && userId.equals(review.getMemberId());
        vo.setCanReply(isReviewOwner && latestReply != null
                && latestReply.getSenderType() == ReviewReplySenderTypeEnum.MERCHANT.getValue());
        vo.setCanEditReply(isReviewOwner && latestReply != null
                && latestReply.getSenderType() == ReviewReplySenderTypeEnum.MEMBER.getValue());
        if (!isReviewOwner) {
            vo.setReplyStatusText(latestReply == null ? "暂无商家回复" : "商家已回复");
        } else if (latestReply == null) {
            vo.setReplyStatusText("等待商家回复");
        } else if (latestReply.getSenderType() == ReviewReplySenderTypeEnum.MERCHANT.getValue()) {
            vo.setReplyStatusText("商家已回复，等待你的回复");
        } else {
            vo.setReplyStatusText("已回复商家，等待商家回复");
        }
        return vo;
    }
}
