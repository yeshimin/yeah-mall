package com.yeshimin.yeahboot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRoleEntity extends BaseEntity<SysRoleEntity> {

    /**
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;
}
