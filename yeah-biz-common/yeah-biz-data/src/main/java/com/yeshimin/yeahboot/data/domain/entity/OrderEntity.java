package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order")
public class OrderEntity extends ShopConditionBaseEntity<OrderEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态：1-待付款 2-待发货 3-待收货 4-交易成功 5-交易关闭 6-退款 7-售后
     */
    private String status;

    /**
     * 退款状态：0-无 1-申请中 2-处理中 3-退款成功 4-已拒绝
     */
    private String refundStatus;

    /**
     * 售后状态：0-无 1-申请中 2-处理中 3-售后完成 4-已驳回
     */
    private String afterSaleStatus;

    /**
     * 是否已评价
     */
    private Boolean reviewed;

    /**
     * 微信预支付ID
     */
    private String wxPrepayId;

    /**
     * 支付成功时间
     */
    private LocalDateTime paySuccessTime;

    /**
     * 支付超时时间
     */
    private LocalDateTime payExpireTime;

    /**
     * 订单关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 订单关闭原因
     */
    private String closeReason;

    // 收货方地址相关 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 收货方姓名
     */
    private String receiverName;

    /**
     * 收货方联系方式（手机号或电话号）
     */
    private String receiverContact;

    /**
     * 收货方省份编码
     */
    private String receiverProvinceCode;

    /**
     * 收货方省份名称
     */
    private String receiverProvinceName;

    /**
     * 收货方城市编码
     */
    private String receiverCityCode;

    /**
     * 收货方城市名称
     */
    private String receiverCityName;

    /**
     * 收货方区县编码
     */
    private String receiverDistrictCode;

    /**
     * 收货方区县名称
     */
    private String receiverDistrictName;

    /**
     * 收货方详细地址
     */
    private String receiverDetailAddress;

    /**
     * 收货方完整地址（冗余：省市区+详细地址）
     */
    private String receiverFullAddress;

    /**
     * 收货方邮政编码
     */
    private String receiverPostalCode;

    // 收货方收货地址相关 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // 发货方发货地址相关 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 发货方姓名
     */
    private String shipperName;

    /**
     * 发货方联系方式（手机号或电话号）
     */
    private String shipperContact;

    /**
     * 发货方省份编码
     */
    private String shipperProvinceCode;

    /**
     * 发货方省份名称
     */
    private String shipperProvinceName;

    /**
     * 发货方城市编码
     */
    private String shipperCityCode;

    /**
     * 发货方城市名称
     */
    private String shipperCityName;

    /**
     * 发货方区县编码
     */
    private String shipperDistrictCode;

    /**
     * 发货方区县名称
     */
    private String shipperDistrictName;

    /**
     * 发货方详细地址
     */
    private String shipperDetailAddress;

    /**
     * 发货方完整地址（冗余：省市区+详细地址）
     */
    private String shipperFullAddress;

    /**
     * 发货方邮政编码
     */
    private String shipperPostalCode;

    // 发货方发货地址相关 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * 快递公司编码
     */
    private String deliveryProviderCode;

    /**
     * 快递单号
     */
    private String trackingNo;

    /**
     * 发货时间（手动发货时该时间不一定准确）
     */
    private LocalDateTime shipTime;

    /**
     * 签收超时时间
     */
    private LocalDateTime receiveExpireTime;

    /**
     * 签收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 签收备注
     */
    private String receiveRemark;
}
