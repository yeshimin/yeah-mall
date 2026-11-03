package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantQueryDto extends BaseDomain {

    /**
     * 商家关键词
     */
    private String keyword;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}
