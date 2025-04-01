package com.yeshimin.yeahboot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色与资源关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_res")
public class SysRoleResEntity extends BaseEntity<SysRoleResEntity> {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 资源ID
     */
    private Long resId;
}
