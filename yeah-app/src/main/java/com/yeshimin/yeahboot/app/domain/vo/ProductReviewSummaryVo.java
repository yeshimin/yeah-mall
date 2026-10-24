package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品评价摘要
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductReviewSummaryVo extends BaseDomain {

    /**
     * 总评价数
     */
    private Long totalCount;

    /**
     * 有图评价数
     */
    private Long imageCount;

    /**
     * 好评数
     */
    private Long goodCount;

    /**
     * 好评率，百分比整数
     */
    private Integer goodRate;

    /**
     * 精选评价
     */
    private List<ProductReviewListVo> topReviews;
}
