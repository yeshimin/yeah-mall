package com.yeshimin.yeahboot.data.domain.base;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopConditionBaseEntity<T extends MchConditionBaseEntity<T>> extends MchConditionBaseEntity<T> {

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空", groups = {Create.class, Query.class})
//    @Null(message = "店铺ID必须为空", groups = {Update.class})
    private Long shopId;
}
