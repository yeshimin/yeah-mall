package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商家关联店铺视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantShopVo extends BaseDomain {

    /**
     * 店铺ID
     */
    private Long id;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 平台是否启用
     */
    private Boolean isEnabled;

    /**
     * 商家是否营业
     */
    private Boolean isOpen;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
