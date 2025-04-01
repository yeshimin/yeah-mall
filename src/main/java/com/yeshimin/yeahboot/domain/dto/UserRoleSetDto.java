package com.yeshimin.yeahboot.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class UserRoleSetDto {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色ID集合
     */
    @NotNull(message = "角色ID集合不能为空")
    private Set<Long> roleIds;
}
