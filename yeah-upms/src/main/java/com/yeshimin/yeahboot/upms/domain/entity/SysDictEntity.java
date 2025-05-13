package com.yeshimin.yeahboot.upms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
public class SysDictEntity extends ConditionBaseEntity<SysDictEntity> {

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 路径
     */
    private String path;

    /**
     * 排序：大于等于1
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
