package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import lombok.Data;

import java.util.List;

/**
 * 评价详情
 */
@Data
public class ReviewDetailVo {

    /**
     * 评价主记录
     */
    private ProductSkuReviewEntity review;

    /**
     * 评价图片
     */
    private List<String> images;

    /**
     * 公开回复列表
     */
    private List<ReviewReplyVo> replies;
}
