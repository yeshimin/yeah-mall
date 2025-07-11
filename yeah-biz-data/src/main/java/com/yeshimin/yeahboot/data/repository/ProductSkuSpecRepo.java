package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuSpecEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSkuSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    /**
     * findListBySkuId
     */
    public List<ProductSkuSpecEntity> findListBySkuId(Long skuId) {
        if (skuId == null) {
            throw new IllegalArgumentException("skuId不能为空");
        }
        return lambdaQuery().eq(ProductSkuSpecEntity::getSkuId, skuId).list();
    }

    /**
     * findListBySkuIds
     */
    public List<ProductSkuSpecEntity> findListBySkuIds(Collection<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return new ArrayList<>();
        }
        return lambdaQuery().in(ProductSkuSpecEntity::getSkuId, skuIds).list();
    }
}
