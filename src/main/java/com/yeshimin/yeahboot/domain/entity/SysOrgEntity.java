package com.yeshimin.yeahboot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统组织表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_org")
public class SysOrgEntity extends BaseEntity<SysOrgEntity> {

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
