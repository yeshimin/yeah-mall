package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.upms.domain.entity.SysUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRolesAndResourcesVo extends BaseDomain {

    /**
     * 用户信息
     */
    private SysUserEntity user;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 资源列表
     */
    private List<String> resources;
}
