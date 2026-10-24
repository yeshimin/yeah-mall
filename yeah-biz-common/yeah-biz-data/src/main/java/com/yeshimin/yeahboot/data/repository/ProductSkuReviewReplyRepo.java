package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewReplyEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSkuReviewReplyMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 商品评价公开回复仓库
 */
@Repository
public class ProductSkuReviewReplyRepo extends BaseRepo<ProductSkuReviewReplyMapper, ProductSkuReviewReplyEntity> {

    /**
     * 查询评价的全部回复
     */
    public List<ProductSkuReviewReplyEntity> findListByReviewIds(Collection<Long> reviewIds) {
        if (reviewIds == null || reviewIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery()
                .in(ProductSkuReviewReplyEntity::getReviewId, reviewIds)
                .orderByAsc(ProductSkuReviewReplyEntity::getCreateTime)
                .orderByAsc(ProductSkuReviewReplyEntity::getId)
                .list();
    }

    /**
     * 锁定当前评价的最新回复
     */
    public ProductSkuReviewReplyEntity findLatestByReviewIdForUpdate(Long reviewId) {
        return baseMapper.findLatestByReviewIdForUpdate(reviewId);
    }
}
