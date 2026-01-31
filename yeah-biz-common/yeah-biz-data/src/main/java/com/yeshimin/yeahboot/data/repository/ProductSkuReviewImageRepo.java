package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewImageEntity;
import com.yeshimin.yeahboot.data.mapper.ProductReviewImageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductSkuReviewImageRepo extends BaseRepo<ProductReviewImageMapper, ProductSkuReviewImageEntity> {

    /**
     * 保存
     */
    public boolean saveImage(ProductSkuReviewEntity review, List<String> images) {
        if (images == null || images.isEmpty()) {
            return false;
        }

        List<ProductSkuReviewImageEntity> list = images.stream().map(image -> {
            ProductSkuReviewImageEntity entity = new ProductSkuReviewImageEntity();
            // 归属信息
            entity.setMchId(review.getMchId());
            entity.setShopId(review.getShopId());
            entity.setMemberId(review.getMemberId());
            entity.setOrderId(review.getId());
            entity.setOrderNo(review.getOrderNo());
            // 商品信息
            entity.setSkuId(review.getSkuId());
            entity.setSpuId(review.getSpuId());
            // 主要业务信息
            entity.setReviewId(review.getId());
            entity.setImage(image);
            return entity;
        }).collect(Collectors.toList());
        boolean r = super.saveBatch(list);
        log.info("productReviewImage.saveBatch result:{}", r);
        return r;
    }
}
