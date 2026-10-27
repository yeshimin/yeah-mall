package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.common.enums.FeedbackStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 平台处理用户反馈 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackHandleDto extends BaseDomain {

    /**
     * 反馈ID
     */
    @NotNull(message = "反馈ID不能为空")
    private Long feedbackId;

    /**
     * 处理状态
     */
    @NotBlank(message = "处理状态不能为空")
    @EnumValue(enumClass = FeedbackStatusEnum.class, message = "处理状态不正确")
    private String status;

    /**
     * 平台回复
     */
    @Size(max = 500, message = "平台回复不能超过500个字符")
    private String replyContent;
}
