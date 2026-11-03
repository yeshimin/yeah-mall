package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 申请退款DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplyRefundDto extends BaseDomain {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 退款原因
     */
    @NotBlank(message = "退款原因不能为空")
    @Size(max = 64, message = "退款原因不能超过64个字符")
    private String reason;

    /**
     * 补充说明
     */
    @Size(max = 500, message = "补充说明不能超过500个字符")
    private String description;

    /**
     * 退款凭证图片文件键
     */
    @Size(max = 6, message = "退款凭证图片最多上传6张")
    private List<String> images;
}
