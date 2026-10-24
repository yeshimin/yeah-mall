package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.ReviewQueryDto;
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
                                            @Param("hasImage") Boolean hasImage,
                                            @Param("goodOnly") Boolean goodOnly);

    /**
     * 分页查询后台评价列表。
     */
    Page<ProductSkuReviewEntity> queryBackstagePage(Page<ProductSkuReviewEntity> page,
                                                     @Param("mchId") Long mchId,
                                                     @Param("shopId") Long shopId,
                                                     @Param("query") ReviewQueryDto query);

    /**
     * 锁定评价主记录，串行化交替回复操作
     */
    ProductSkuReviewEntity findByIdForUpdate(@Param("id") Long id);
}
