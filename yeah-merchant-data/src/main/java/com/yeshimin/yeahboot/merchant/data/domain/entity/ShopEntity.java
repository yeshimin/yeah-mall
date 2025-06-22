package com.yeshimin.yeahboot.merchant.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 店铺表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_shop")
public class ShopEntity extends ConditionBaseEntity<ShopEntity> {

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;
}
