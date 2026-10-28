package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.PlatProductCategoryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatProductCategoryTreeNodeVo extends PlatProductCategoryEntity {

    /**
     * 子节点集合
     */
    private List<PlatProductCategoryTreeNodeVo> children;
}
