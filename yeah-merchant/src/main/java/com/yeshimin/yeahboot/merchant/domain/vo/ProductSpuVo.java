package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品spu VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuVo extends ProductSpuEntity {

    /**
     * 商品分类名称
     */
    private String categoryName;
}
