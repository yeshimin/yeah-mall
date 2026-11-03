package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.ProductFavoritesEntity;
import com.yeshimin.yeahboot.data.domain.vo.ProductFavoriteCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ProductFavoritesMapper extends BaseMapper<ProductFavoritesEntity> {

    /**
     * 按商品统计收藏人数
     */
    List<ProductFavoriteCountVo> selectCountBySpuIds(@Param("spuIds") Collection<Long> spuIds);
}
