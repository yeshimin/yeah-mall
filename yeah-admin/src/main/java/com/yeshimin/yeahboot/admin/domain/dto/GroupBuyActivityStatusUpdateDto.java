package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 拼团活动状态更新参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyActivityStatusUpdateDto extends BaseDomain {

    /**
     * 拼团活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long id;

    /**
     * 目标活动状态
     */
    @NotBlank(message = "活动状态不能为空")
    private String status;

}
