package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductCategoryTreeNodeVo extends ProductCategoryEntity {

    /**
     * 子节点集合
     */
    private List<ProductCategoryTreeNodeVo> children;
}
