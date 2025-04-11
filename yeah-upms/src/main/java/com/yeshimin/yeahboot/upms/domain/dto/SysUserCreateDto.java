package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.common.enums.DataStatusEnum;
import com.yeshimin.yeahboot.upms.common.validation.EnumValue;
import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserCreateDto extends BaseDomain {

    /**
     * 组织ID集合
     */
    private Set<Long> orgIds;

    /**
     * 岗位ID集合
     */
    private Set<Long> postIds;

    /**
     * 角色ID集合
     */
    private Set<Long> roleIds;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 状态：1-启用 2-禁用
     */
    @EnumValue(enumClass = DataStatusEnum.class)
    private String status;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别：1-男性 2-女性
     */
    private String gender;

    /**
     * 备注
     */
    private String remark;
}
