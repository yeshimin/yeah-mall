package com.yeshimin.yeahboot.data.domain.base;

import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class MchConditionBaseEntity<T extends ConditionBaseEntity<T>> extends ConditionBaseEntity<T> {

    /**
     * 商户ID
     * 操作商家的数据，都不需要指定mchId，会自动填充
     */
//    @Null(message = "商家ID必须为空", groups = {Create.class, Query.class, Update.class})
    private Long mchId;
}
