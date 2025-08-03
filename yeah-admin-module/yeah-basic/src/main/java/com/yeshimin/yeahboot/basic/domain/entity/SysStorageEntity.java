package com.yeshimin.yeahboot.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统存储表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_storage")
public class SysStorageEntity extends ConditionBaseEntity<SysStorageEntity> {

    /**
     * 存储类型
     */
    private Integer storageType;

    /**
     * 是否成功
     */
    @JsonIgnore
    @TableField(exist = false)
    private Boolean success;

    /**
     * 基础路径（只有本地存储才有）
     */
    private String basePath;

    /**
     * bucket
     */
    private String bucket;

    /**
     * 路径（可为空）
     */
    private String path;

    /**
     * 文件key（无后缀）
     */
    private String fileKey;

    /**
     * 文件后缀（可为空）
     */
    private String suffix;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 是否公开
     */
    private Boolean isPublic;
}