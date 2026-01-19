package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryProviderSetPopularDto extends ShopDataBaseDomain {

    /**
     * 主键ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 是否主流
     */
    @NotNull(message = "是否主流不能为空")
    private Boolean isPopular;
}
