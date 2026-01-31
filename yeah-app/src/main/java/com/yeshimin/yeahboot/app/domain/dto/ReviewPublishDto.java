package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评价发布DTO
 * 订单评价+商品SKU评价
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "评价发布DTO")
public class ReviewPublishDto extends BaseDomain {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long orderId;

    /**
     * 明细评价集合
     */
    @Valid
    @NotEmpty(message = "明细评价集合不能为空")
    @Schema(description = "明细评价集合")
    private List<ReviewPublishItemDto> items;

    /**
     * 描述相符：1-5
     */
    @Min(value = 1, message = "综合评价不能小于1")
    @Max(value = 5, message = "综合评价不能大于5")
    @NotNull(message = "描述相符不能为空")
    @Schema(description = "描述相符：1-5")
    private Integer descriptionRating;

    /**
     * 物流服务：1-5
     */
    @Min(value = 1, message = "综合评价不能小于1")
    @Max(value = 5, message = "综合评价不能大于5")
    @NotNull(message = "物流服务不能为空")
    @Schema(description = "物流服务：1-5")
    private Integer deliveryRating;

    /**
     * 服务态度：1-5
     */
    @Min(value = 1, message = "综合评价不能小于1")
    @Max(value = 5, message = "综合评价不能大于5")
    @NotNull(message = "服务态度不能为空")
    @Schema(description = "服务态度：1-5")
    private Integer serviceRating;

    /**
     * 是否匿名
     */
    @NotNull(message = "是否匿名不能为空")
    @Schema(description = "是否匿名")
    private Boolean isAnonymous;
}
