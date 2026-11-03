package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    /**
     * 店铺logo
     */
    private String shopLogo;

    /**
     * 店铺简介
     */
    @Size(max = 500, message = "店铺简介不能超过500个字符")
    private String shopIntro;

    /**
     * 商家是否营业
     */
    private Boolean isOpen;
}
