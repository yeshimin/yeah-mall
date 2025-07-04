package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecOptMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;

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
}
