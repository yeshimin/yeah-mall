package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.upms.domain.entity.SysUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthVo extends BaseDomain {

    /**
     * 是否认证通过
     */
    private Boolean authenticated;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 主题（子系统）
     */
    private String subject;

    /**
     * 用户信息
     */
    private SysUserEntity user;

    /**
     * 角色列表
     */
    private List<String> roles = new ArrayList<>(0);

    /**
     * 资源列表
     */
    private List<String> resources = new ArrayList<>(0);
}
