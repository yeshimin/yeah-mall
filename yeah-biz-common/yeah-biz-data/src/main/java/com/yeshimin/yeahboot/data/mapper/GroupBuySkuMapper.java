package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupBuySkuMapper extends BaseMapper<GroupBuySkuEntity> {

    /**
     * 锁定拼团SKU。
     */
    GroupBuySkuEntity selectByIdForUpdate(@Param("id") Long id);

    /**
     * 扣减拼团库存。
     */
    int occurStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 回补拼团库存。
     */
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
