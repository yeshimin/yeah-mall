package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.data.domain.vo.ProductSpecVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品spu详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuDetailVo extends ProductSpuVo {

    /**
     * 规格配置信息
     */
    private List<ProductSpecVo> specs;
}
