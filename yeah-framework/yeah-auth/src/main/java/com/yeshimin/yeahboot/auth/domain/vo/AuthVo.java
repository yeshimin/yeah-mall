package com.yeshimin.yeahboot.auth.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

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
     * 主题（子系统/模块）
     */
    private String subject;

    /**
     * 终端
     */
    private String terminal;

    /**
     * 用户信息
     */
    private String username;

    /**
     * 角色列表
     */
    private Set<String> roles = new HashSet<>(0);

    /**
     * 资源列表
     */
    private Set<String> resources = new HashSet<>(0);
}
