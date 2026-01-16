package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员收货地址
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member_address")
public class MemberAddressEntity extends ConditionBaseEntity<MemberAddressEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 联系方式（手机号或电话号）
     */
    private String contact;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区县编码
     */
    private String districtCode;

    /**
     * 区县名称
     */
    private String districtName;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 完整地址（冗余：省市区+详细地址）
     */
    private String fullAddress;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 是否默认
     */
    private Boolean isDefault;
}
