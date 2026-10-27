package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分兑换订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_points_order")
public class PointsOrderEntity extends ConditionBaseEntity<PointsOrderEntity> {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 客户端幂等请求号
     */
    private String requestNo;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 积分商品SPU ID
     */
    private Long spuId;

    /**
     * 积分商品SKU ID
     */
    private Long skuId;

    /**
     * 商品名称快照
     */
    private String productName;

    /**
     * SKU名称快照
     */
    private String skuName;

    /**
     * 商品图片快照
     */
    private String productImage;

    /**
     * 商品类型
     */
    private Integer productType;

    /**
     * 履约方式
     */
    private Integer fulfillmentType;

    /**
     * 兑换数量
     */
    private Integer quantity;

    /**
     * 单件积分价格
     */
    private Long pointsPrice;

    /**
     * 兑换总积分
     */
    private Long totalPoints;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 自动发放的会员优惠券ID
     */
    private Long memberCouponId;

    /**
     * 收货人姓名快照
     */
    private String receiverName;

    /**
     * 收货人联系方式快照
     */
    private String receiverContact;

    /**
     * 收货地址快照
     */
    private String receiverFullAddress;

    /**
     * 物流公司编码
     */
    private String deliveryProviderCode;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 履约说明
     */
    private String fulfillmentRemark;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;
}
