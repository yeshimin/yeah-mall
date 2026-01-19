package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysOrgTreeQueryDto extends BaseQueryDto {

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
