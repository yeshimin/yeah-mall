package com.yeshimin.yeahboot.auth.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
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
    private List<String> roles = new ArrayList<>(0);

    /**
     * 资源列表
     */
    private List<String> resources = new ArrayList<>(0);
}
