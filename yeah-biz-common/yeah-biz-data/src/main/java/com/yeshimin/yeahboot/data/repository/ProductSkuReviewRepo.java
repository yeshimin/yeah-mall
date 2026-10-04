package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSkuReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductSkuReviewRepo extends BaseRepo<ProductSkuReviewMapper, ProductSkuReviewEntity> {

    private final ProductSkuReviewMapper productSkuReviewMapper;

    /**
     * createOne
     */
    public ProductSkuReviewEntity createOne(OrderItemEntity orderItem,
                                            Integer overallRating, String content, Boolean isAnonymous,
                                            String nickname, String avatar) {
        ProductSkuReviewEntity review = new ProductSkuReviewEntity();
        // 归属信息
        review.setMchId(orderItem.getMchId());
        review.setShopId(orderItem.getShopId());
        review.setMemberId(orderItem.getMemberId());
        review.setOrderId(orderItem.getId());
        review.setOrderNo(orderItem.getOrderNo());
        // 商品信息
        review.setSkuId(orderItem.getSkuId());
        review.setSkuName(orderItem.getSkuName());
        review.setSpuId(orderItem.getSpuId());
        review.setSpuName(orderItem.getSpuName());
        review.setSpuMainImage(orderItem.getSpuMainImage());
        // 业务主要信息
        review.setOverallRating(overallRating);
        review.setContent(content);
        review.setIsAnonymous(isAnonymous);
        // 买家信息
        if (!review.getIsAnonymous()) {
            review.setNickname(nickname);
            review.setAvatar(avatar);
        } else {
            review.setNickname("匿名买家");
            review.setAvatar("");
        }
        review.insert();
        return review;
    }

    /**
     * 分页查询商品评价列表
     */
    public Page<ProductSkuReviewEntity> queryPage(Page<ProductSkuReviewEntity> page, Long spuId, Boolean hasImage) {
        return productSkuReviewMapper.queryPage(page, spuId, hasImage);
    }
}
