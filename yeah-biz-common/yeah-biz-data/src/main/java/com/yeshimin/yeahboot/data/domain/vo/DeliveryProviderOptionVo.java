package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物流服务商选项
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryProviderOptionVo extends BaseDomain {

    /**
     * 物流服务商编码
     */
    private String code;

    /**
     * 物流服务商名称
     */
    private String name;
}
