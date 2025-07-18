package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopUpdateDto extends BaseDomain {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = {Update.class})
    private Long id;

    /**
     * 店铺名称
     */
    private String shopName;
}
