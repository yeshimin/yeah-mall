package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员微信身份表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member_wx_identity")
public class MemberWxIdentityEntity extends ConditionBaseEntity<MemberWxIdentityEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 微信应用AppID
     */
    private String appId;

    /**
     * 当前AppID下的OpenID
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String openId;

    /**
     * 微信开放平台UnionID
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String unionId;
}
