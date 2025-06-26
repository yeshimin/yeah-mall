package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 乡村表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("area_village")
public class AreaVillageEntity extends ConditionBaseEntity<AreaVillageEntity> {

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
