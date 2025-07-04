package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;

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
}
