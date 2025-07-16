package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.MchResEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商家资源节点
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MchResTreeNodeVo extends MchResEntity {

    /**
     * 子节点集合
     */
    private List<MchResTreeNodeVo> children;
}
