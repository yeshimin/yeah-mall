package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPostSetDto extends BaseDomain {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 岗位ID集合
     */
    @NotNull(message = "岗位ID集合不能为空")
    private Set<Long> postIds;
}
