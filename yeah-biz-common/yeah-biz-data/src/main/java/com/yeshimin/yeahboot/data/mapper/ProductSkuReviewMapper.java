package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductSkuReviewMapper extends BaseMapper<ProductSkuReviewEntity> {

    /**
     * 分页查询商品评价列表
     */
    Page<ProductSkuReviewEntity> queryPage(Page<ProductSkuReviewEntity> page,
                                           @Param("spuId") Long spuId,
                                           @Param("hasImage") Boolean hasImage);
}
