package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分商品SPU
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_points_spu")
public class PointsSpuEntity extends ConditionBaseEntity<PointsSpuEntity> {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品副标题
     */
    private String subtitle;

    /**
     * 商品类型：1实物，2优惠券，3虚拟权益
     */
    private Integer productType;

    /**
     * 履约方式：1物流发货，2自动发券，3人工履约
     */
    private Integer fulfillmentType;

    /**
     * 商品主图文件Key
     */
    private String mainImage;

    /**
     * 商品详情
     */
    private String detailDesc;

    /**
     * 自动发放的优惠券ID
     */
    private Long couponId;

    /**
     * 每人限兑数量，0表示不限
     */
    private Integer perMemberLimit;

    /**
     * 兑换开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 兑换结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否上架
     */
    private Boolean isEnabled;

    /**
     * 排序
     */
    private Integer sort;
}
