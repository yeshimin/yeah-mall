package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.PointsSpuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * App积分商品列表视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointsProductVo extends PointsSpuEntity {

    /**
     * 最低积分价格
     */
    private Long minPointsPrice;

    /**
     * 可用总库存
     */
    private Integer stock;
}
