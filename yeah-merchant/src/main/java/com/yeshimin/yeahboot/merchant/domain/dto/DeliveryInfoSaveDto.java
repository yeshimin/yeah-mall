package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryInfoSaveDto extends ShopDataBaseDomain {

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 联系方式（手机号或电话号）
     */
    @NotBlank(message = "联系方式不能为空")
    private String contact;

    /**
     * 省份编码
     */
    @NotBlank(message = "省份编码不能为空")
    private String provinceCode;

    /**
     * 省份名称
     */
    @NotBlank(message = "省份名称不能为空")
    private String provinceName;

    /**
     * 城市编码
     */
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;

    /**
     * 城市名称
     */
    @NotBlank(message = "城市名称不能为空")
    private String cityName;

    /**
     * 区县编码
     */
    @NotBlank(message = "区县编码不能为空")
    private String districtCode;

    /**
     * 区县名称
     */
    @NotBlank(message = "区县名称不能为空")
    private String districtName;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 邮政编码
     */
    @Length(max = 6, message = "邮政编码长度不能超过6位")
    private String postalCode;
}
