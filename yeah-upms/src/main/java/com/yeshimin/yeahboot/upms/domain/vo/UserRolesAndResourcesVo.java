package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRolesAndResourcesVo extends BaseDomain {

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 资源列表
     */
    private List<String> resources;
}
