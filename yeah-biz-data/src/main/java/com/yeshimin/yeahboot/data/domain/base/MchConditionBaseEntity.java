package com.yeshimin.yeahboot.data.domain.base;

import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Null;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class MchConditionBaseEntity<T extends ConditionBaseEntity<T>> extends ConditionBaseEntity<T> {

    /**
     * 商户ID
     */
    @Null(message = "商家ID必须为空", groups = {Update.class})
    private Long mchId;
}
