package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ProductSpuRepo extends BaseRepo<ProductSpuMapper, ProductSpuEntity> {

    /**
     * countByShopIdAndName
     */
    public long countByShopIdAndName(Long shopId, String name) {
        return lambdaQuery().eq(ProductSpuEntity::getShopId, shopId).eq(ProductSpuEntity::getName, name).count();
    }
}
