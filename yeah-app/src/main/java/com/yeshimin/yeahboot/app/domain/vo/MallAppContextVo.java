package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商城App上下文
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MallAppContextVo extends BaseDomain {

    /**
     * 当前构建目标是否可用
     */
    private Boolean available;

    /**
     * 不可用原因
     */
    private String unavailableReason;

    /**
     * App模式：PLATFORM平台商城，SHOP独立店铺
     */
    private String appMode;

    /**
     * 构建目标：platform或店铺编号
     */
    private String appTarget;

    /**
     * 绑定店铺ID
     */
    private Long shopId;

    /**
     * 绑定店铺编号
     */
    private String shopNo;

    /**
     * 绑定店铺名称
     */
    private String shopName;

    /**
     * 绑定店铺Logo
     */
    private String shopLogo;

    /**
     * 商家是否营业
     */
    private Boolean shopOpen;
}
