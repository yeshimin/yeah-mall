package com.yeshimin.yeahboot.data.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 评价公开状态更新参数
 */
@Data
public class ReviewVisibilityDto {

    /**
     * 评价ID
     */
    @NotNull(message = "评价ID不能为空")
    private Long id;

    /**
     * 是否公开
     */
    @NotNull(message = "公开状态不能为空")
    private Boolean isVisible;

    /**
     * 处理备注
     */
    @Length(max = 255, message = "处理备注不能超过255字")
    private String visibilityRemark;
}
