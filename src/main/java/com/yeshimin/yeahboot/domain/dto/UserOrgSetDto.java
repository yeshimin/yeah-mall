package com.yeshimin.yeahboot.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class UserOrgSetDto {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 组织ID集合
     */
    @NotNull(message = "组织ID集合不能为空")
    private Set<Long> orgIds;
}
