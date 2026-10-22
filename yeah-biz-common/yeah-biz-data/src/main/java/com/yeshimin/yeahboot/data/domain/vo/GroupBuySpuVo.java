package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拼团商品视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuySpuVo extends BaseDomain {

    /**
     * 拼团商品SPU ID
     */
    private Long id;

    /**
     * 拼团活动ID
     */
    private Long activityId;

    /**
     * 拼团活动名称
     */
    private String activityName;

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 商家名称
     */
    private String mchName;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 销量
     */
    private Long sales;

    /**
     * 最低原价
     */
    private BigDecimal minOriginPrice;

    /**
     * 最低拼团价
     */
    private BigDecimal minGroupPrice;

    /**
     * 最高原价
     */
    private BigDecimal maxOriginPrice;

    /**
     * 最高拼团价
     */
    private BigDecimal maxGroupPrice;
    /**
     * 最终展示主图，自定义图优先，原始SPU主图兜底
     */
    private String mainImage;

    /**
     * 拼团自定义主图
     */
    private String customMainImage;

    /**
     * 原始SPU主图
     */
    private String sourceMainImage;

    /**
     * 商品详情描述
     */
    private String detailDesc;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 每团成团人数
     */
    private Integer minPeople;

    /**
     * 拼团库存
     */
    private Integer groupStock;

    /**
     * 报名商品创建时间
     */
    private LocalDateTime createTime;

    /**
     * 报名商品更新时间
     */
    private LocalDateTime updateTime;
}
