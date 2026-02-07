package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台快捷入口表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("plat_quick_entry")
public class PlatQuickEntryEntity extends ConditionBaseEntity<PlatQuickEntryEntity> {

    /**
     * 名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 业务类型
     */
    private Integer type;

    /**
     * 跳转目标
     */
    private String target;

    /**
     * 排序：大于等于1
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 备注
     */
    private String remark;
}
