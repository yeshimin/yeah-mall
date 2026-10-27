package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 积分商品SKU保存参数
 */
@Data
public class PointsSkuSaveDto {

    /**
     * SKU ID，新增时为空
     */
    private Long id;

    /**
     * 积分商品SPU ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long spuId;

    /**
     * SKU名称
     */
    @NotBlank(message = "SKU名称不能为空")
    private String name;

    /**
     * 单件积分价格
     */
    @NotNull(message = "积分价格不能为空")
    @Min(value = 1, message = "积分价格不能小于1")
    private Long pointsPrice;

    /**
     * 当前可用库存
     */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;

    /**
     * SKU主图文件Key，为空时使用商品主图
     */
    private String mainImage;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isEnabled;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;
}
