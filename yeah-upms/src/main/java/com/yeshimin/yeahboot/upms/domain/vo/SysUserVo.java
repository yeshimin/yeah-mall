package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.IdNameVo;
import com.yeshimin.yeahboot.upms.domain.entity.SysUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserVo extends SysUserEntity {

    /**
     * 岗位
     */
    private List<IdNameVo> posts;

    /**
     * 组织
     */
    private List<IdNameVo> orgs;

    /**
     * 角色
     */
    private List<IdNameVo> roles;
}
