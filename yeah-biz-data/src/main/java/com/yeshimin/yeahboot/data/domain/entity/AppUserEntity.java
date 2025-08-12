package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * app端用户表
 */
@Schema(description = "app端用户表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_user")
public class AppUserEntity extends ConditionBaseEntity<AppUserEntity> {

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String mobile;

    /**
     * 登录密码
     */
    @Schema(description = "登录密码")
    private String password;
}
