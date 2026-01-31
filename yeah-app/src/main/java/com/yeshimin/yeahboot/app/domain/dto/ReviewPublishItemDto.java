package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 商品评价发布-明细评价DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品评价发布-明细评价DTO")
public class ReviewPublishItemDto extends BaseDomain {

    /**
     * 订单明细ID
     */
    @NotNull(message = "订单明细ID不能为空")
    @Schema(description = "订单明细ID")
    private Long orderItemId;

    /**
     * 综合评价：1-5
     */
    @Min(value = 1, message = "综合评价不能小于1")
    @Max(value = 5, message = "综合评价不能大于5")
    @NotNull(message = "综合评价不能为空")
    @Schema(description = "综合评价：1-5")
    private Integer overallRating;

    /**
     * 评价内容
     */
    @Length(max = 255, message = "评价内容不能超过255字")
    @Schema(description = "评价内容")
    private String content;

    /**
     * 图片集合
     */
    @Size(max = 6, message = "图片集合不能超过6张")
    @NotNull(message = "图片集合不能为空")
    @Schema(description = "图片集合")
    private List<String> images;
}
