package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatProductCategoryTreeNodeVo extends ProductCategoryEntity {

    /**
     * 子节点集合
     */
    private List<PlatProductCategoryTreeNodeVo> children;
}
