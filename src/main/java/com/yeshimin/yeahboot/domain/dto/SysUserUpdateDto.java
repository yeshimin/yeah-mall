package com.yeshimin.yeahboot.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class SysUserUpdateDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 组织ID集合
     */
    private Set<Long> orgIds;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 备注
     */
    private String remark;
}
