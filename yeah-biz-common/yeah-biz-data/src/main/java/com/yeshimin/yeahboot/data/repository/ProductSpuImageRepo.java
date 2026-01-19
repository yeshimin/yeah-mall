package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuImageEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuImageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
public class ProductSpuImageRepo extends BaseRepo<ProductSpuImageMapper, ProductSpuImageEntity> {

    /**
     * 统计ids中不属于mchId的数据
     */
    public long countByIdsAndNotMchId(Long mchId, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids不能为空");
        }
        return lambdaQuery().in(ProductSpuImageEntity::getId, ids).ne(ProductSpuImageEntity::getMchId, mchId).count();
    }

    /**
     * findListBySpuId
     */
    public List<ProductSpuImageEntity> findListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().eq(ProductSpuImageEntity::getSpuId, spuId).list();
    }
}
