package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecDefEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecDefMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Slf4j
@Repository
public class ProductSpecDefRepo extends BaseRepo<ProductSpecDefMapper, ProductSpecDefEntity> {

    /**
     * countByShopIdAndSpecName
     */
    public long countByShopIdAndSpecName(Long shopId, String specName) {
        return lambdaQuery()
                .eq(ProductSpecDefEntity::getShopId, shopId)
                .eq(ProductSpecDefEntity::getSpecName, specName)
                .count();
    }

    /**
     * 统计ids中不属于mchId的数据
     */
    public long countByIdsAndNotMchId(Long mchId, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids不能为空");
        }
        return lambdaQuery().in(ProductSpecDefEntity::getId, ids).ne(ProductSpecDefEntity::getMchId, mchId).count();
    }

    /**
     * countByIdAndShopId
     */
    public long countByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery()
                .eq(ProductSpecDefEntity::getId, id)
                .eq(ProductSpecDefEntity::getShopId, shopId)
                .count();
    }
}
