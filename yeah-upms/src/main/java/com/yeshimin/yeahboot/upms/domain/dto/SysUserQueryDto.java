package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.common.config.mybatis.Query;
import com.yeshimin.yeahboot.upms.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.upms.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class SysUserQueryDto extends BaseQueryDto {

    /**
     * 用户名
     */
    @QueryField(QueryField.Type.LIKE_RIGHT)
    private String username;

    /**
     * 密码
     */
    @QueryField(QueryField.Type.LIKE)
    private String password;
}
