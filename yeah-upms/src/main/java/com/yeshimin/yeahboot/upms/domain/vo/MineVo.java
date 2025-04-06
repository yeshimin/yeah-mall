package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import com.yeshimin.yeahboot.upms.domain.entity.SysOrgEntity;
import com.yeshimin.yeahboot.upms.domain.entity.SysRoleEntity;
import com.yeshimin.yeahboot.upms.domain.entity.SysUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MineVo extends BaseDomain {

    /**
     * 用户信息
     */
    private SysUserEntity user;

    /**
     * 角色信息
     */
    private List<SysRoleEntity> roles;

    /**
     * 组织信息
     */
    private List<SysOrgEntity> orgs;

    /**
     * 权限集合
     */
    private List<String> permissions;
}
