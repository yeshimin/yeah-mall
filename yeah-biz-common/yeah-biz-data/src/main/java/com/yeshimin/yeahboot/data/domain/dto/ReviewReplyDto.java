package com.yeshimin.yeahboot.data.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评价回复参数
 */
@Data
public class ReviewReplyDto {

    /**
     * 评价ID
     */
    @NotNull(message = "评价ID不能为空")
    private Long id;

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    @Length(max = 500, message = "回复内容不能超过500字")
    private String replyContent;
}
