package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Slf4j
@Repository
public class ProductSpuRepo extends BaseRepo<ProductSpuMapper, ProductSpuEntity> {

    /**
     * countByShopIdAndName
     */
    public long countByShopIdAndName(Long shopId, String name) {
        return lambdaQuery().eq(ProductSpuEntity::getShopId, shopId).eq(ProductSpuEntity::getName, name).count();
    }

    /**
     * 统计ids中不属于mchId的数据
     */
    public long countByIdsAndNotMchId(Long mchId, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids不能为空");
        }
        return lambdaQuery().in(ProductSpuEntity::getId, ids).ne(ProductSpuEntity::getMchId, mchId).count();
    }

    /**
     * countByIdAndShopId
     */
    public long countByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery().eq(ProductSpuEntity::getId, id).eq(ProductSpuEntity::getShopId, shopId).count();
    }

    /**
     * findOneByIdAndShopId
     */
    public ProductSpuEntity findOneByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery().eq(ProductSpuEntity::getId, id).eq(ProductSpuEntity::getShopId, shopId).one();
    }

    /**
     * countByShopId
     */
    public long countByShopId(Long shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("shopId不能为空");
        }
        return lambdaQuery().eq(ProductSpuEntity::getShopId, shopId).count();
    }
}
