package com.yeshimin.yeahboot.merchant.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 店铺下数据基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ShopDataBaseDomain extends BaseQueryDto {

    /**
     * 商家ID
     * 操作商家的数据，都不需要指定mchId，会自动填充
     */
    @JsonIgnore
    @QueryField(type = QueryField.Type.EQ)
    private Long mchId;

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空", groups = {Create.class, Query.class})
    @QueryField(type = QueryField.Type.EQ)
    private Long shopId;
}
