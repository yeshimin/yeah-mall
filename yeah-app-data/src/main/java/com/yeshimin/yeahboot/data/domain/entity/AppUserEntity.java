package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * app端用户表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_user")
public class AppUserEntity extends ConditionBaseEntity<AppUserEntity> {

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 登录密码
     */
    private String password;
}
