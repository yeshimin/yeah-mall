package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberAddressUpdateDto extends BaseDomain {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    private Long id;

    /**
     * 姓名
     */
    @Length(max = 16, message = "姓名长度不能超过16个字符")
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 联系方式（手机号或电话号）
     */
    @Length(max = 16, message = "联系方式长度不能超过16个字符")
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
    @Length(max = 100, message = "详细地址长度不能超过100个字符")
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 邮政编码
     */
    @Length(max = 6, message = "邮政编码长度不能超过6位")
    private String postalCode;

    /**
     * 是否默认
     */
    private Boolean isDefault;
}
