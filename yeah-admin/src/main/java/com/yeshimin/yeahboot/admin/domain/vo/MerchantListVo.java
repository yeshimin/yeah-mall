package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商家列表视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantListVo extends BaseDomain {

    /**
     * 商家ID
     */
    private Long id;

    /**
     * 登录账号
     */
    private String loginAccount;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 店铺数量
     */
    private Long shopCount;

    /**
     * 启用店铺数量
     */
    private Long enabledShopCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
