package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.ProductBrowseHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 商品浏览足迹Mapper
 */
@Mapper
public interface ProductBrowseHistoryMapper extends BaseMapper<ProductBrowseHistoryEntity> {

    /**
     * 新增或更新浏览足迹。
     */
    int upsert(@Param("memberId") Long memberId, @Param("spuId") Long spuId,
               @Param("mchId") Long mchId, @Param("shopId") Long shopId,
               @Param("browseTime") LocalDateTime browseTime);
}
