package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.upms.domain.entity.SysDictEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统字典节点
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictTreeNodeVo extends SysDictEntity {

    /**
     * 子节点集合
     */
    private List<SysDictTreeNodeVo> children;
}
