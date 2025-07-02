package com.yeshimin.yeahboot.common.domain.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeshimin.yeahboot.common.common.config.mybatis.Query;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带查询条件字段的基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public abstract class ConditionBaseEntity<T extends BaseEntity<T>> extends BaseEntity<T> {

    /**
     * 客户端自定义查询条件，需要在@Query中显式开启
     * 添加@JsonProperty注解，使在接口请求时暴露该字段，响应时不返回该字段
     * 添加@TableField注解，标识该字段非表字段
     * 命名添加_后缀，避免和表字段冲突（前缀方式jackson默认解析不了）
     */
    @JsonProperty(value = CommonConsts.CONDITIONS_FIELD_NAME, access = JsonProperty.Access.WRITE_ONLY)
    @TableField(exist = false)
    private String conditions_;
}
