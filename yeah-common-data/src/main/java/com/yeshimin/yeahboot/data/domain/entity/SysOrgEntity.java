package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统组织表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_org")
public class SysOrgEntity extends ConditionBaseEntity<SysOrgEntity> {

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态：1-启用 2-禁用
     */
    private String status;

    /**
     * 排序：大于等于1
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
