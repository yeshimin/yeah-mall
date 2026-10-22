package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 拼团报名提交参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyApplySubmitDto extends BaseDomain {

    /**
     * 拼团活动ID
     */
    @NotNull(message = "拼团活动ID不能为空")
    private Long activityId;

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

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
     * 自定义拼团主图，为空时引用原始SPU主图
     */
    private String mainImage;

    /**
     * 商品详细描述
     */
    @NotBlank(message = "商品详细描述不能为空")
    private String detailDesc;

    /**
     * 本次新增到富文本描述中的图片
     */
    private List<String> detailImages;

    /**
     * 自定义拼团轮播图，为空时引用原始SPU轮播图
     */
    private List<String> images;

    @Valid
    @NotEmpty(message = "SKU集合不能为空")
    private List<GroupBuyApplySkuItemDto> skuList;

    /**
     * 申请备注
     */
    private String applyRemark;
}
