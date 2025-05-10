package com.yeshimin.yeahboot.upms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.upms.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 城市表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("area_city")
public class AreaCityEntity extends ConditionBaseEntity<AreaCityEntity> {

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
