package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecOptMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductSpecOptRepo extends BaseRepo<ProductSpecOptMapper, ProductSpecOptEntity> {

    /**
     * countByOptIds
     */
    public long countByOptIds(Collection<Long> optIds) {
        if (optIds == null || optIds.isEmpty()) {
            throw new IllegalArgumentException("optIds不能为空");
        }
        return lambdaQuery().in(ProductSpecOptEntity::getOptId, optIds).count();
    }

    /**
     * countBySpecIds
     */
    public long countBySpecIds(Collection<Long> specIds) {
        if (specIds == null || specIds.isEmpty()) {
            throw new IllegalArgumentException("specIds不能为空");
        }
        return lambdaQuery().in(ProductSpecOptEntity::getSpecId, specIds).count();
    }

    /**
     * findListBySpuId
     */
    public List<ProductSpecOptEntity> findListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().eq(ProductSpecOptEntity::getSpuId, spuId).list();
    }

    /**
     * findOptIdListBySpuId
     */
    public List<Long> findOptIdListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().select(ProductSpecOptEntity::getOptId)
                .eq(ProductSpecOptEntity::getSpuId, spuId).list()
                .stream().map(ProductSpecOptEntity::getOptId).collect(Collectors.toList());
    }

    /**
     * deleteBySpuId
     */
    public boolean deleteBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaUpdate().eq(ProductSpecOptEntity::getSpuId, spuId).remove();
    }

    /**
     * deleteBySpuIds
     */
    public boolean deleteBySpuIds(Collection<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return false;
        }
        return lambdaUpdate().in(ProductSpecOptEntity::getSpuId, spuIds).remove();
    }
}
