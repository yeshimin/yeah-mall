package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 拼团商品详情视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyProductDetailVo extends GroupBuySpuVo {

    /**
     * 拼团SKU列表
     */
    private List<GroupBuySkuEntity> skus;
}