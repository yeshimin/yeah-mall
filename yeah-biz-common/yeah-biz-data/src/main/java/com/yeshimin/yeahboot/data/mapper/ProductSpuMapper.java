package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.ShopProductCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ProductSpuMapper extends BaseMapper<ProductSpuEntity> {

    /**
     * 按店铺统计商品数量
     */
    List<ShopProductCountVo> selectCountByShopIds(@Param("shopIds") Collection<Long> shopIds);
}
