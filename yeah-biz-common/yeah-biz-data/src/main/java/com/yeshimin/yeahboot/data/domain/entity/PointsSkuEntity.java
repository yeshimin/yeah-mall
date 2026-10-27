package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分商品SKU
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_points_sku")
public class PointsSkuEntity extends ConditionBaseEntity<PointsSkuEntity> {

    /**
     * 积分商品SPU ID
     */
    private Long spuId;

    /**
     * SKU名称
     */
    private String name;

    /**
     * 单件积分价格
     */
    private Long pointsPrice;

    /**
     * 可用库存
     */
    private Integer stock;

    /**
     * 累计配置库存
     */
    private Integer totalStock;

    /**
     * SKU主图文件Key
     */
    private String mainImage;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 排序
     */
    private Integer sort;
}
