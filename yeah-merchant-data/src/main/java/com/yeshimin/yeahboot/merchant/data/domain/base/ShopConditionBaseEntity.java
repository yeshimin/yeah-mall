package com.yeshimin.yeahboot.merchant.data.domain.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopConditionBaseEntity<T extends MchConditionBaseEntity<T>> extends MchConditionBaseEntity<T> {

    /**
     * 店铺ID
     */
    private Long shopId;
}
