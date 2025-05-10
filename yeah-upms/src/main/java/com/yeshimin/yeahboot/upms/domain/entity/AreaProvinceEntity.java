package com.yeshimin.yeahboot.upms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.upms.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 省份表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("area_province")
public class AreaProvinceEntity extends ConditionBaseEntity<AreaProvinceEntity> {

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;
}
