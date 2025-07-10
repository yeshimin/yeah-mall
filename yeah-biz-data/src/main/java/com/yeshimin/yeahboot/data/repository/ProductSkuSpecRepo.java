package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuSpecEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSkuSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Slf4j
@Repository
public class ProductSkuSpecRepo extends BaseRepo<ProductSkuSpecMapper, ProductSkuSpecEntity> {

    /**
     * deleteBySkuId
     */
    public boolean deleteBySkuId(Long skuId) {
        if (skuId == null) {
            throw new IllegalArgumentException("skuId不能为空");
        }
        return lambdaUpdate().eq(ProductSkuSpecEntity::getSkuId, skuId).remove();
    }

    /**
     * deleteBySkuIds
     */
    public boolean deleteBySkuIds(Collection<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            throw new IllegalArgumentException("skuIds不能为空");
        }
        return lambdaUpdate().in(ProductSkuSpecEntity::getSkuId, skuIds).remove();
    }
}
