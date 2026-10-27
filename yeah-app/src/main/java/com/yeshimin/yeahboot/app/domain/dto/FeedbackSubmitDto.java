package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户提交意见反馈 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackSubmitDto extends BaseDomain {

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 500, message = "反馈内容不能超过500个字符")
    private String content;

    /**
     * 联系方式
     */
    @Size(max = 50, message = "联系方式不能超过50个字符")
    private String contact;

    /**
     * 反馈图片文件键
     */
    @Size(max = 6, message = "反馈图片最多上传6张")
    private List<String> images;
}
