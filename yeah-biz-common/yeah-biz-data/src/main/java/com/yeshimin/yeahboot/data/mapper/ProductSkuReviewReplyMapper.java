package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewReplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品评价公开回复 Mapper
 */
@Mapper
public interface ProductSkuReviewReplyMapper extends BaseMapper<ProductSkuReviewReplyEntity> {

    /**
     * 锁定当前评价的最新回复
     */
    ProductSkuReviewReplyEntity findLatestByReviewIdForUpdate(@Param("reviewId") Long reviewId);
}
