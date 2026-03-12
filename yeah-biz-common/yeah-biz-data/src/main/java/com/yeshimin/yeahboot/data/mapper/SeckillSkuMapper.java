package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SeckillSkuMapper extends BaseMapper<SeckillSkuEntity> {

    /**
     * 扣减库存
     */
    boolean occurStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加库存
     */
    boolean increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
