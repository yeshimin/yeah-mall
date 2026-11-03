package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.MchConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 店铺表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_shop")
public class ShopEntity extends MchConditionBaseEntity<ShopEntity> {

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String shopLogo;

    /**
     * 店铺简介
     */
    private String shopIntro;

    /**
     * 平台是否启用
     */
    private Boolean isEnabled;

    /**
     * 商家是否营业
     */
    private Boolean isOpen;

    /**
     * 平台结算佣金比例，单位百分比
     */
    private BigDecimal settlementCommissionRate;

    /**
     * 订单完成后延迟结算天数
     */
    private Integer settlementDelayDays;
}
