package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member")
public class MemberEntity extends ConditionBaseEntity<MemberEntity> {

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 登录密码
     */
    private String password;
}
