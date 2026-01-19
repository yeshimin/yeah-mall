package com.yeshimin.yeahboot.common.domain.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeshimin.yeahboot.common.common.config.mybatis.Query;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class BaseQueryDto extends BaseDomain {

    /**
     * 客户端自定义查询条件，需要在@Query中显式开启
     * 命名添加_后缀，避免和表字段冲突（前缀方式jackson默认解析不了）
     */
    @JsonProperty(value = CommonConsts.CONDITIONS_FIELD_NAME)
    private String conditions_;
}
