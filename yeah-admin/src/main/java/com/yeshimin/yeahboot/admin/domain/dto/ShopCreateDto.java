package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopCreateDto extends BaseDomain {

    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Long mchId;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;
}
