package com.yeshimin.yeahboot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统资源表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_res")
public class SysResEntity extends ConditionBaseEntity<SysResEntity> {

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;
}
