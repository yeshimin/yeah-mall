package com.yeshimin.yeahboot.domain.vo;

import com.yeshimin.yeahboot.domain.entity.SysResEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统资源节点
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysResTreeNodeVo extends SysResEntity {

    /**
     * 子节点集合
     */
    private List<SysResTreeNodeVo> children;
}
