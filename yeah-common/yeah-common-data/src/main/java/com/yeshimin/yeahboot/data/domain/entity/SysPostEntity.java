package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统岗位表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
public class SysPostEntity extends ConditionBaseEntity<SysPostEntity> {

    /**
     * 编码
     */
    private String code;

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
