package com.yeshimin.yeahboot.data.domain.base;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopConditionBaseEntity<T extends MchConditionBaseEntity<T>> extends MchConditionBaseEntity<T> {

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空", groups = {Create.class})
    private Long shopId;
}
