package com.yeshimin.yeahboot.upms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.upms.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户与岗位关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_post")
public class SysUserPostEntity extends ConditionBaseEntity<SysUserPostEntity> {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 岗位ID
     */
    private Long postId;
}
