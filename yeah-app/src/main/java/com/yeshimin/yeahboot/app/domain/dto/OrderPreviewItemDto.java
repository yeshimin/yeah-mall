package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.Query;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 预览订单DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class OrderPreviewItemDto extends BaseDomain {

    /**
     * 商品SKU ID
     */
    @NotNull(message = "商品SKU ID不能为空")
    private Long skuId;

    /**
     * 数量
     */
    @Min(value = 1, message = "数量不能小于1")
    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
