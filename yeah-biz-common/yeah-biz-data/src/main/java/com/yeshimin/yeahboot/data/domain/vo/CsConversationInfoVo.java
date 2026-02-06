package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客服会话信息VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CsConversationInfoVo extends BaseDomain {

    /**
     * 会话ID
     */
    private Long id;

    /**
     * 买家ID
     */
    private Long memberId;

    /**
     * 买家昵称
     */
    private String memberNickname;

    /**
     * 买家头像
     */
    private String memberAvatar;

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 商家名称
     */
    private String mchNickname;

    /**
     * 商家头像
     */
    private String mchAvatar;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String shopLogo;
}
