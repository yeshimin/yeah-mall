package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 商家状态更新参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantStatusUpdateDto extends BaseDomain {

    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Long id;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isEnabled;
}
