package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductSpecRepo extends BaseRepo<ProductSpecMapper, ProductSpecEntity> {

    /**
     * countBySpecIds
     */
    public Long countBySpecIds(Collection<Long> specIds) {
        if (specIds == null || specIds.isEmpty()) {
            throw new IllegalArgumentException("specIds不能为空");
        }
        return lambdaQuery().in(ProductSpecEntity::getSpecId, specIds).count();
    }

    /**
     * findListBySpuId
     */
    public List<ProductSpecEntity> findListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().eq(ProductSpecEntity::getSpuId, spuId).list();
    }

    /**
     * findSpecIdListBySpuId
     */
    public List<Long> findSpecIdListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().select(ProductSpecEntity::getSpecId)
                .eq(ProductSpecEntity::getSpuId, spuId).list()
                .stream().map(ProductSpecEntity::getSpecId).collect(Collectors.toList());
    }

    /**
     * deleteBySpuId
     */
    public boolean deleteBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaUpdate().eq(ProductSpecEntity::getSpuId, spuId).remove();
    }

    /**
     * deleteBySpuIds
     */
    public boolean deleteBySpuIds(Collection<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return false;
        }
        return lambdaUpdate().in(ProductSpecEntity::getSpuId, spuIds).remove();
    }
}
