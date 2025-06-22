package com.yeshimin.yeahboot.merchant.data.domain.entity;

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
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
