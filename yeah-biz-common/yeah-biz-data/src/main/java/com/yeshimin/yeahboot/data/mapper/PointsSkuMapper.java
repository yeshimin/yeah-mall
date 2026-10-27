package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.PointsSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 积分商品SKU Mapper
 */
@Mapper
public interface PointsSkuMapper extends BaseMapper<PointsSkuEntity> {

    /**
     * 条件扣减可用库存
     */
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity,
                    @Param("enabled") Boolean enabled);

    /**
     * 回补取消订单占用的库存
     */
    int restoreStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 按差值调整可用库存与累计库存
     */
    int adjustStock(@Param("id") Long id, @Param("delta") Integer delta);
}
