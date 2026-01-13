package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家物流提供商表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_delivery_provider")
public class DeliveryProviderEntity extends ShopConditionBaseEntity<DeliveryProviderEntity> {

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否默认（只能启用一个）
     */
    private Boolean isDefault;
}
