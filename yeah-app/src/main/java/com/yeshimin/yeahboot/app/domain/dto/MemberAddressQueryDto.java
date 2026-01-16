package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberAddressQueryDto extends BaseQueryDto {

    /**
     * 姓名
     */
    @QueryField(type = QueryField.Type.LIKE)
    private String name;

    /**
     * 联系方式（手机号或电话号）
     */
    @QueryField(type = QueryField.Type.LIKE)
    private String contact;

    /**
     * 完整地址
     */
    @QueryField(type = QueryField.Type.LIKE)
    private String fullAddress;

    /**
     * 是否默认
     */
    @QueryField(type = QueryField.Type.EQ)
    private Boolean isDefault;
}
