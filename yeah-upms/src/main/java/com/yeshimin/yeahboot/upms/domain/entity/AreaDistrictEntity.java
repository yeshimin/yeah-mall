package com.yeshimin.yeahboot.upms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.upms.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 区县表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("area_district")
public class AreaDistrictEntity extends ConditionBaseEntity<AreaDistrictEntity> {

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
