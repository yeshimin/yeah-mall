package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 拼团活动启用状态更新参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyActivityEnabledUpdateDto extends BaseDomain {

    /**
     * 拼团活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long id;

    /**
     * 是否启用
     */
    @NotNull(message = "是否启用不能为空")
    private Boolean isEnabled;
}
