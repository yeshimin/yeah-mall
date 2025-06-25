package com.yeshimin.yeahboot.merchant.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.ProductSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ProductSkuRepo extends BaseRepo<ProductSkuMapper, ProductSkuEntity> {

    /**
     * countByIdAndShopId
     */
    public long countByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery().eq(ProductSkuEntity::getId, id).eq(ProductSkuEntity::getShopId, shopId).count();
    }

    /**
     * countBySpuIdAndName
     */
    public long countBySpuIdAndName(Long spuId, String name) {
        return lambdaQuery().eq(ProductSkuEntity::getSpuId, spuId).eq(ProductSkuEntity::getName, name).count();
    }
}
