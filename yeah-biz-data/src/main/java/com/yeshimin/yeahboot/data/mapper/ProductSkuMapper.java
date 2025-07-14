package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSkuEntity> {

    /**
     * 扣减库存
     */
    boolean occurStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
