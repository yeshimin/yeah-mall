package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryProviderSetDefaultDto extends ShopDataBaseDomain {

    /**
     * 主键ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 是否默认
     */
    @NotNull(message = "是否默认不能为空")
    private Boolean isDefault;
}
