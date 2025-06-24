package com.yeshimin.yeahboot.merchant.data.domain.base;

import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MchConditionBaseEntity<T extends ConditionBaseEntity<T>> extends ConditionBaseEntity<T> {

    /**
     * 商户ID
     */
    private Long mchId;
}
