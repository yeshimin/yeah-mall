package com.yeshimin.yeahboot.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 街道表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("area_street")
public class AreaStreetEntity extends ConditionBaseEntity<AreaStreetEntity> {

    /**
     * 父级编码
     */
    private String parentCode;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;
}
