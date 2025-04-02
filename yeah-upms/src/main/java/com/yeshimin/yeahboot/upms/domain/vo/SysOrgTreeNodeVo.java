package com.yeshimin.yeahboot.upms.domain.vo;

import com.yeshimin.yeahboot.upms.domain.entity.SysOrgEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统组织节点
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOrgTreeNodeVo extends SysOrgEntity {

    /**
     * 子节点集合
     */
    private List<SysOrgTreeNodeVo> children;
}
