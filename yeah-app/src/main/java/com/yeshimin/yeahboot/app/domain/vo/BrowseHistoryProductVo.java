package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * App浏览足迹商品视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrowseHistoryProductVo extends BaseDomain {

    /**
     * 商品SPU ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 最后浏览时间
     */
    private LocalDateTime lastBrowseTime;

    /**
     * 浏览次数
     */
    private Long browseCount;

    /**
     * 商品是否仍可用
     */
    private Boolean available;
}
