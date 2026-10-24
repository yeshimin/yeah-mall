package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单评价详情
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderReviewDetailVo extends BaseDomain {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 评价时间
     */
    private LocalDateTime reviewTime;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * 描述相符评分
     */
    private Integer descriptionRating;

    /**
     * 物流服务评分
     */
    private Integer deliveryRating;

    /**
     * 服务态度评分
     */
    private Integer serviceRating;

    /**
     * 商品评价
     */
    private List<ProductReviewListVo> items;
}
