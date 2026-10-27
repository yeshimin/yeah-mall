package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 积分商品保存参数
 */
@Data
public class PointsProductSaveDto {

    /**
     * 商品ID，新增时为空
     */
    private Long id;

    /**
     * 分类ID
     */
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品副标题
     */
    private String subtitle;

    /**
     * 商品类型：1实物，2优惠券，3虚拟权益
     */
    @NotNull(message = "商品类型不能为空")
    private Integer productType;

    /**
     * 履约方式：1物流发货，2自动发券，3人工履约
     */
    @NotNull(message = "履约方式不能为空")
    private Integer fulfillmentType;

    /**
     * 商品主图文件Key
     */
    @NotBlank(message = "商品主图不能为空")
    private String mainImage;

    /**
     * 商品详情
     */
    private String detailDesc;

    /**
     * 自动发放的优惠券ID
     */
    private Long couponId;

    /**
     * 每人限兑数量，0表示不限
     */
    @NotNull(message = "每人限兑数量不能为空")
    @Min(value = 0, message = "每人限兑数量不能小于0")
    private Integer perMemberLimit;

    /**
     * 兑换开始时间
     */
    @NotNull(message = "兑换开始时间不能为空")
    private LocalDateTime beginTime;

    /**
     * 兑换结束时间
     */
    @NotNull(message = "兑换结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 是否上架
     */
    @NotNull(message = "上架状态不能为空")
    private Boolean isEnabled;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;
}
