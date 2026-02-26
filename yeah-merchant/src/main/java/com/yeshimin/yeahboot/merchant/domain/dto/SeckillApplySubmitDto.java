package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class     SeckillApplySubmitDto extends BaseDomain {

    /**
     * 场次ID
     */
    @NotNull(message = "场次ID不能为空")
    private Long sessionId;

    /**
     * 原始商品SPU ID
     */
    @NotNull(message = "原始商品SPU ID不能为空")
    private Long spuId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 主图
     */
    @NotBlank(message = "主图不能为空")
    private String mainImage;

    /**
     * 商品详细描述
     */
    @NotBlank(message = "商品详细描述不能为空")
    private String detailDesc;

    /**
     * 轮播图
     */
    @NotEmpty(message = "轮播图不能为空")
    private List<String> images;

    /**
     * SKU集合
     */
    @Valid
    @NotEmpty(message = "SKU集合不能为空")
    private List<SeckillApplySkuItemDto> skuList;

    /**
     * 申请备注
     */
    private String applyRemark;
}
