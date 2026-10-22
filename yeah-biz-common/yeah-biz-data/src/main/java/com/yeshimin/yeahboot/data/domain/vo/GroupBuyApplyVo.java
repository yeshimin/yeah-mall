package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拼团报名视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyApplyVo extends BaseDomain {

    /**
     * 报名申请ID
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
     * 拼团商品SPU ID
     */
    private Long groupBuySpuId;

    /**
     * 商品名称
     */
    private String spuName;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 最低拼团价
     */
    private BigDecimal minGroupPrice;

    /**
     * 最高拼团价
     */
    private BigDecimal maxGroupPrice;

    /**
     * 拼团库存
     */
    private Integer groupStock;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 申请备注
     */
    private String applyRemark;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 申请提交时间
     */
    private LocalDateTime createTime;

    /**
     * 审核或最近更新时间
     */
    private LocalDateTime updateTime;
}
