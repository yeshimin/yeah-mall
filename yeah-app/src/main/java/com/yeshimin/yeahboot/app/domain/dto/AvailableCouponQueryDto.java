package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * app端可使用优惠券列表查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AvailableCouponQueryDto extends BaseDomain {

    /**
     * 商品项
     */
    @Valid
    @NotEmpty(message = "商品项不能为空")
    private List<OrderPreviewItemDto> items;
}
