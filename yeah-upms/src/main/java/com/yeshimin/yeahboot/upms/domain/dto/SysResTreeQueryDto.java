package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.common.config.mybatis.Query;
import com.yeshimin.yeahboot.upms.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.upms.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class SysResTreeQueryDto extends BaseQueryDto {

    /**
     * 名称
     */
    @QueryField(QueryField.Type.LIKE)
    private String name;

    /**
     * 状态：1-启用 2-禁用
     */
    @QueryField(QueryField.Type.EQ)
    private String status;

    public boolean isQuery() {
        return name != null && !name.isEmpty() || status != null && !status.isEmpty();
    }
}
